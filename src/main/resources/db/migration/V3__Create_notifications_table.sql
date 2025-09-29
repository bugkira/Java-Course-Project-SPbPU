-- V3__Create_notifications_table.sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    message TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    related_task_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    viewed BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (related_task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_recipient_id ON notifications(recipient_id);
CREATE INDEX idx_notifications_related_task_id ON notifications(related_task_id);
CREATE INDEX idx_notifications_viewed ON notifications(viewed);
CREATE INDEX idx_notifications_timestamp ON notifications(timestamp);
