# Avans DevOps Project

This project implements a Scrum/DevOps project management system similar to Azure DevOps or Jira. It allows teams to manage projects using Scrum methodology, with features for backlog management, sprint planning, discussions, and DevOps pipeline integration.

## Design Patterns Implemented

This project implements several design patterns to achieve a maintainable and extensible architecture:

### 1. State Pattern

The State pattern is used to model the states of Backlog Items and Sprints, allowing for clean state transitions with appropriate validation rules.

**Implementation:**
- BacklogItem states: Todo → Doing → ReadyForTesting → Testing → Tested → Done
- Release Sprint states: Created → InProgress → Releasing → Finished → Released → Closed
- Review Sprint states: Created → InProgress → Reviewing → Reviewed → Closed

Each state is encapsulated in its own class, with the context (BacklogItem/Sprint) delegating state-specific behavior to the current state object.

### 2. Observer Pattern

The Observer pattern is used to implement notifications throughout the system, allowing interested components to be notified of relevant events.

**Implementation:**
- Subject abstract class with addObserver, removeObserver, and notifyObservers methods
- IObserver interface implemented by TeamMember and other classes
- Used for backlog item state changes, discussion updates, and sprint status changes

### 3. Strategy Pattern

The Strategy pattern is used to encapsulate different algorithms that can be selected at runtime.

**Implementation:**
- Notification strategies: EmailNotification and SlackNotification
- Pipeline run strategies: FailFastStrategy and AlwaysContinueStrategy
- Report generation strategies: PdfReportStrategy and PngReportStrategy

### 4. Decorator Pattern

The Decorator pattern is used to add additional responsibilities to reports dynamically.

**Implementation:**
- IReport interface defines the base interface
- ConcreteReport implements the base functionality
- HeaderDecorator and FooterDecorator add header and footer to reports
- Allows combining decorators in any order

### 5. Template Method Pattern

The Template Method pattern is used in the pipeline execution process, defining a skeleton algorithm in a method with some steps deferred to subclasses.

**Implementation:**
- PipelineStep abstract class with runStep template method
- Concrete steps (SourceStep, BuildStep, etc.) implement specific behavior
- Ensures consistent execution flow while allowing step-specific customization

### 6. Composite Pattern

The Composite pattern is used to represent hierarchical structures of discussions, allowing individual messages and threads to be treated uniformly.

**Implementation:**
- DiscussionComponent abstract class defines the interface
- DiscussionMessage represents leaf nodes
- DiscussionThread represents composite nodes containing other components
- Enables unified operations like lock/unlock across the entire discussion hierarchy

### 7. Factory Pattern

The Factory Method pattern is used to create pipeline steps without specifying their concrete classes.

**Implementation:**
- PipelineStepFactory creates different types of pipeline steps
- Centralizes the creation logic and abstracts the instantiation process
- Allows for easy addition of new step types in the future

## Business Logic Highlights

- BacklogItem can only move to Done state when all Activities are completed
- Discussions are automatically locked when related BacklogItem is marked as Done
- Release Sprint triggers pipeline execution and notifies stakeholders of results
- Sprint properties cannot be modified after sprint has started
- Pipeline execution strategies determine how failures are handled

## Testing

Unit tests cover key business logic, focusing on:
- State transitions and validation rules
- Notification flow between components
- Pipeline execution with different strategies
- Locking/unlocking of discussion threads

## SonarQube Integration

The code architecture supports SonarQube integration for automatic code quality analysis, ensuring that:
- Code follows clean code principles
- Test coverage meets quality standards
- No major code smells or bugs are present
