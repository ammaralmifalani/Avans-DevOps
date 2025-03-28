package com.avans.domain.backlog.state;

import com.avans.domain.backlog.BacklogItem;



public class DoingState implements IBacklogState {
    private static final String NAME = "Doing";

    @Override
    public void moveToNext(BacklogItem item) {
        item.setState(new ReadyForTestingState());
    }

    @Override
    public void revertToTodo(BacklogItem item) {
        item.setState(new TodoState());
    }

    @Override
    public String getName() {
        return NAME;
    }
    
}
