package com.avans.domain.backlog.state;

import com.avans.domain.backlog.BacklogItem;


public class TestingState implements IBacklogState {
    private static final String NAME = "Testing";
    @Override
    public void moveToNext(BacklogItem item) {
        item.setState(new TestedState());
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
