package com.subscriptionhub.notification.model;

public class CheckNotificationRequest {
    private int usuarioId;
    private int diasAntecedencia = 3;

    public CheckNotificationRequest() {}

    public CheckNotificationRequest(int usuarioId, int diasAntecedencia) {
        this.usuarioId = usuarioId;
        this.diasAntecedencia = diasAntecedencia;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getDiasAntecedencia() {
        return diasAntecedencia;
    }

    public void setDiasAntecedencia(int diasAntecedencia) {
        this.diasAntecedencia = diasAntecedencia;
    }
}

