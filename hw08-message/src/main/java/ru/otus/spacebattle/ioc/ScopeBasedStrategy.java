package ru.otus.spacebattle.ioc;

import ru.otus.spacebattle.command.*;
import ru.otus.spacebattle.domain.UObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Стратегия разрешения зависимостей на основе скоупов
 */
public class ScopeBasedStrategy implements Strategy {

    static final String ROOT_SCOPE_NAME = "ROOT";

    private final ThreadLocal<String> currentScope;
    private final ThreadLocal<Map<String, Scope>> scopes;
    private Scope rootScope = null;

    public ScopeBasedStrategy() {
        this.currentScope = ThreadLocal.withInitial(() -> ROOT_SCOPE_NAME);
        this.scopes = ThreadLocal.withInitial(HashMap::new);
    }

    @Override
    public Object resolve(String key, Object... args) {
        if ("Scopes.Root".equals(key)) {
            return rootScope; // корневой скоуп доступен из любого потока
        } else {
            Scope scope = getCurrentOrRootScope();
            if (scope == null) {
                throw new IllegalStateException("Scope not found");
            }
            return scope.resolve(key, args);
        }
    }

    /**
     * Текущий скоуп (соответствующий потоку)
     * Если текущий не установлен, то возвращается рутовый скоуп (из любого скоупа)
     */
    private String getCurrentOrRootScopeName() {
        String scope = currentScope.get();
        if (scope == null) {
            scope = ROOT_SCOPE_NAME;
        }
        return scope;
    }

    private Scope getCurrentOrRootScope() {
        return getScope(getCurrentOrRootScopeName());
    }

    private Scope getScope(String scope) {
        if (ROOT_SCOPE_NAME.equals(scope)) {
            return rootScope;
        }
        return scopes.get().get(scope);
    }

    /**
     * Команда для инициализации зависимостей данной стратегии
     */
    public class InitScopeBasedIoCCommand implements Command {

        @Override
        public synchronized void execute() {
            if (rootScope != null) {
                return; // уже инициализировано
            }

            Map<String, Function<Object[], Object>> dependencies = new ConcurrentHashMap<>();

            // базовый скоуп
            Scope scope = new ChildScope(
                dependencies,
                new RootScope(IoC.resolve("IoC.Default")) // крайний скоуп с дефолтной стратегией
            );

            // зависимость: создание хранилища зависимостей для скоупа
            dependencies.put("Scopes.Storage", args -> {
                return new ConcurrentHashMap<String, Function<Object[], Object>>();
            });

            // зависимость: создание нового скоупа
            dependencies.put("Scopes.New", args -> {
                return (Command) () -> {
                    // проверяем имя скоупа
                    String newScopeName = (String) args[1];
                    if (newScopeName == null) {
                        throw new IllegalArgumentException("Not valid scope name");
                    }
                    if (scopes.get().containsKey(newScopeName)) {
                        throw new IllegalArgumentException(String.format("Scope %s already exists", newScopeName));
                    }
                    // создаём скоуп с родительским из параметров
                    String parentScopeName = (String) args[0];
                    Scope parentScope;
                    if (parentScopeName == null || (parentScope = getScope(parentScopeName)) == null) {
                        throw new IllegalArgumentException(String.format("Parent scope %s not found", parentScopeName));
                    }
                    // получаем хранилище зависимостей
                    Map<String, Function<Object[], Object>> storage = IoC.resolve("Scopes.Storage");
                    // создаём скоуп, помещаем в хранилище скоупов
                    ChildScope childScope = new ChildScope(storage, parentScope);
                    scopes.get().put((String) args[1], childScope);
                };
            });

            // зависимость: получение текущего скоупа (или дефолтного, т.е. корневого)
            dependencies.put("Scopes.Current", args -> {
                return getCurrentOrRootScopeName();
            });

            // зависимость: установка текущего скоупа
            dependencies.put("Scopes.Current.Set", args -> {
                return (Command) () -> {
                    String scopeName = (String) args[0];
                    if (getScope(scopeName) == null) {
                        throw new IllegalArgumentException(String.format("Scope %s not found", scopeName));
                    }
                    ScopeBasedStrategy.this.currentScope.set(scopeName);
                };
            });

            // зависимость: регистрация зависимостей
            dependencies.put("IoC.Register", args -> {
                return (Command) () -> {
                    Scope currentScope = getCurrentOrRootScope();
                    boolean success;
                    if (currentScope == null) {
                        success = false;
                    } else {
                        success = currentScope.addDependency((String) args[0], (Function<Object[], Object>) args[1]);
                    }
                    if (!success) {
                        throw new IllegalArgumentException("Can not register dependency");
                    }
                };
            });

            // зависимость: работа с играми; игры хранятся в корневом скоупе, тогда доступ будет из всех потоков
            Map<String, GameCommand> games = new ConcurrentHashMap<>();

            // получить игру по id
            dependencies.put("Games.GetById", args -> games.get((String) args[0]));

            // создать очередь игры
            dependencies.put("Games.CreateQueue", args -> new CommandConcurrentQueue());

            // зависимость: создание игры с очередью и объектами
            dependencies.put("Games.Create", args -> {
                String gameId = (String) args[0];
                return games.compute(gameId, (id, oldGame) -> {
                    if (oldGame != null) {
                        throw new IllegalArgumentException("Game already exists");
                    }
                    CommandQueue commandQueue = IoC.resolve("Games.CreateQueue", gameId);
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.CommandQueue", id), (Function<Object[], Object>) args1 -> commandQueue)).execute();
                    Map<String, UObject> objects = new ConcurrentHashMap<>();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.Objects.Add", id), (Function<Object[], Object>) args1 -> objects.put((String) args1[0], (UObject) args1[1]))).execute();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.Objects.Get", id), (Function<Object[], Object>) args1 -> objects.get((String) args1[0]))).execute();
                    Map<String, Boolean> allowedOperations = new ConcurrentHashMap<>();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.AllowedOperations.Add", id), (Function<Object[], Object>) args1 -> allowedOperations.put((String) args1[0], true))).execute();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.AllowedOperations.Remove", id), (Function<Object[], Object>) args1 -> allowedOperations.remove((String) args1[0]))).execute();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.AllowedOperations.Get", id), (Function<Object[], Object>) args1 -> allowedOperations.getOrDefault((String) args1[0], false))).execute();
                    return new GameCommand(gameId);
                });
            });

            // зависимость: команда интерпретатор
            dependencies.put("InterpretCommand", args -> new InterpretCommand((String) args[0], (String) args[1], (String) args[2], (Object[]) args[3]));

            // устанавливаем рутовый скоуп
            rootScope = scope;

            // устанавливаем стратегию
            ((Command) IoC.resolve("IoC.SetupStrategy", ScopeBasedStrategy.this)).execute();

            // устанавливаем текущий скоуп как рутовый
            ScopeBasedStrategy.this.currentScope.set(ROOT_SCOPE_NAME);
        }
    }
}
