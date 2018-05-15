package com.ab.lenovo.netminaservice.entity;

import java.io.Serializable;

public class MessageEntity implements Serializable {

    private String userName;
    private String userAvatar;
    private String message;
    private boolean isMyself;

    public MessageEntity() {

    }

    public MessageEntity(String userName, String userAvatar, String message, boolean isMyself) {
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.message = message;
        this.isMyself = isMyself;
    }

    public boolean isMyself() {
        return isMyself;
    }

    public void setMyself(boolean myself) {
        isMyself = myself;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    @Override
    public String toString() {
        return "MessageEntity [userName=" + userName + ", userAvatar=" + userAvatar + ", message=" + message
                + ", isMyself=" + isMyself + "]";
    }

}
