package br.nom.rccrv.code.domain.enums;

public enum StepStatus {

    PENDING(1),
    SUCCESS(2),
    FAILED(3);

    private int code;

    StepStatus(int code) {
        this.code = code;
    }

    public void transitionCode(StepStatus newStatus) {
        this.code = newStatus.code;
    }
}
