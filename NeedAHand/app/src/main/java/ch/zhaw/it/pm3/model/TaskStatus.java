package ch.zhaw.it.pm3.model;

/**
 * This enum represents the status of a task.
 */
public enum TaskStatus {
    IN_PROGRESS("In Bearbeitung"),
    DONE("Erledigt");

    private String status;

    TaskStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString(){
        return status;
    }
}
