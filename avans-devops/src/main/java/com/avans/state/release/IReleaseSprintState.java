package com.avans.state.release;

import com.avans.domain.project.ReleaseSprint;



public interface IReleaseSprintState {
    void start(ReleaseSprint sprint);
    void performRelease(ReleaseSprint sprint);
    void retryRelease(ReleaseSprint sprint);
    void cancelRelease(ReleaseSprint sprint);
    String getName();
}