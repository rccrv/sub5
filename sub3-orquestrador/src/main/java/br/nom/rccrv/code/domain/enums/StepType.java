package br.nom.rccrv.code.domain.enums;

public enum StepType {

    EXECUTION(1),
    ROLLBACK(2);

    private int code;

    StepType(int code) {
        this.code = code;
    }
}
