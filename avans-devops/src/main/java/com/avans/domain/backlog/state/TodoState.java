package com.avans.domain.backlog.state;

import com.avans.domain.backlog.BacklogItem;


public class TodoState implements IBacklogState {
    private static final String NAME = "Todo";

    @Override
    public void moveToNext(BacklogItem item) {
        item.setState(new DoingState());
    }

    @Override
    public void revertToTodo(BacklogItem item) {
        // No action needed, already in Todo state
    }

    @Override
    public String getName() {
        return NAME;
    
}
}
