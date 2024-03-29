# Домашнее задание № 09
Создать микросервис Авторизация пользователя

## Цель:
Применить навыки разработки микросервиса.

## Описание/Пошаговая инструкция выполнения домашнего задания:
При реализации Endpoint для приема входящих сообщений от Агента возникает задача авторизации пользователя 
на отправку сообщений для управления командой танков в конкретной игре, чтобы не допустить вмешательства в процесс игры сторонними пользователями.  
В результате выполнения этого ДЗ будет разработан микросервис, который выдает jwt токен из участников танкового сражения, 
для того, чтобы игровой сервер мог принять решение о возможности выполнения входящего сообщения от имени пользователя.

### Описание задания:
Предполагается реализация микросервиса авторизации с помощью jwt токена.
Алгоритм взаимодействия сервиса авторизации и Игрового сервера следующий:

1. Один из пользователей организует танковый бой и определяет список участников (их может быть больше 2-х).
На сервер авторизации уходит запрос о том, что организуется танковый бой, и присылается список его участников. 
Сервер в ответ возвращает id танкового боя.
2. Аутентифицированный пользователь посылает запрос на выдачу jwt токена, который авторизует право этого пользователя на участие в танковом бое.
Для этого он должен указать в запросе id танкового боя.
Если пользователь был указан в списке участников танкового боя, то он выдает пользователю jwt токен, в котором указан id игры.
3. Пользователь при отправке сообщений в Игровой сервер прикрепляет к сообщениям выданный jwt токен, а сервер при получении сообщения 
удостоверяется, что токен был выдан сервером авторизации (проверят хэш jwt токена), и проверяет, что пользователь запросил выполнение 
операции в игре, в которой он эту операцию может выполнять.
