package com.avans.domain.backlog.state;

import com.avans.domain.backlog.BacklogItem;


public class ReadyForTestingState implements IBacklogState {
    private static final String NAME = "Ready for Testing";
 
    @Override
    public void moveToNext(BacklogItem item) {
        item.setState(new TestingState());
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
