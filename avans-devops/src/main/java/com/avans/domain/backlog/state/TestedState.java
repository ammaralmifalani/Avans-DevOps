package com.avans.domain.backlog.state;

import com.avans.domain.backlog.BacklogItem;

public class TestedState implements IBacklogState {
    private static final String NAME = "Tested";
    @Override
    public void moveToNext(BacklogItem item) {
        item.setState(new DoneState());
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