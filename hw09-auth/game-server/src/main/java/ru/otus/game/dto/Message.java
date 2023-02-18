package ru.otus.game.dto;

/**
 * Сообщение стандартного вида, которое получает игровой сервер
 */
public class Message {

    /**
     * id игры для определения получателя сообщения при маршрутизации сообщения внутри игрового сервера
     */
    private final String gameId;

    /**
     * id игрового объекта, которому адресовано сообщение
     */
    private final String objectId;

    /**
     * id операции для определения команды
     */
    private final String operationId;

    /**
     * параметры команды
     */
    private final Object[] args;

    public Message(String gameId, String objectId, String operationId, Object[] args) {
        this.gameId = gameId;
        this.objectId = objectId;
        this.operationId = operationId;
        this.args = args;
    }

    public String getGameId() {
        return gameId;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getOperationId() {
        return operationId;
    }

    public Object[] getArgs() {
        return args;
    }

    public static class Builder {
        private String gameId;
        private String objectId;
        private String operationId;
        private Object[] args;

        public Builder setGameId(String gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder setObjectId(String objectId) {
            this.objectId = objectId;
            return this;
        }

        public Builder setOperationId(String operationId) {
            this.operationId = operationId;
            return this;
        }

        public Builder setArgs(Object[] args) {
            this.args = args;
            return this;
        }

        public Message build() {
            return new Message(gameId, objectId, operationId, args);
        }
    }

}
