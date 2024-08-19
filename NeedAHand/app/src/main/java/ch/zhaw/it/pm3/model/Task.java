package ch.zhaw.it.pm3.model;

import java.io.Serializable;

/**
 * This class represents a Task.
 */
public class Task extends Offer implements Serializable{
    private int taskId;

    private TaskStatus status;

    public Task(int id, Offer offer) {
        super(offer.getDocumentInfo(), id, offer.getAdvertisementId(), offer.getServiceProviderId());
        this.taskId = id;
        this.status = TaskStatus.IN_PROGRESS;
    }

    public int getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getTitle() + " Status: " + status;
    }
}