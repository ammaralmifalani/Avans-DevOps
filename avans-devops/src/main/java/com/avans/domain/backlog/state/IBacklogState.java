package com.avans.domain.backlog.state;

import com.avans.domain.backlog.BacklogItem;


public interface IBacklogState {
    void moveToNext(BacklogItem item);
    void revertToTodo(BacklogItem item);
    String getName();
}
