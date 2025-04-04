package com.avans.domain.backlog.state;

import com.avans.domain.backlog.BacklogItem;


public class DoneState implements IBacklogState {
    private static final String NAME = "Done";

    @Override
    public void moveToNext(BacklogItem item) {
        // No next state, already done
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
