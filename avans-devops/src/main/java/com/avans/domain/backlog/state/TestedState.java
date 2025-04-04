package com.avans.domain.backlog.state;

import com.avans.domain.backlog.BacklogItem;

public class TestedState implements IBacklogState {
    private static final String NAME = "Tested";
    @Override
    public void moveToNext(BacklogItem item) {
        // Verify all activities are completed before moving to Done
        if (!item.areAllActivitiesDone()) {
            throw new IllegalStateException("Cannot move to Done state: not all activities are completed");
        }
        
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
