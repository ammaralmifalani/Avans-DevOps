package com.avans;

import java.time.LocalDate;

import com.avans.decorator.FooterDecorator;
import com.avans.decorator.HeaderDecorator;
import com.avans.decorator.IReport;
import com.avans.domain.backlog.Activity;
import com.avans.domain.backlog.BacklogItem;
import com.avans.domain.discussions.DiscussionMessage;
import com.avans.domain.discussions.DiscussionThread;
import com.avans.domain.member.Developer;
import com.avans.domain.member.ProductOwner;
import com.avans.domain.member.ScrumMaster;
import com.avans.domain.project.Project;
import com.avans.domain.project.ReleaseSprint;
import com.avans.pipeline.Pipeline;
import com.avans.strategy.notification.EmailNotification;
import com.avans.strategy.notification.SlackNotification;
import com.avans.strategy.pipeline.FailFastStrategy;
import com.avans.strategy.report.PdfReportStrategy;


public class Main {
    public static void main(String[] args) {
        LocalDate startDate = LocalDate.of(2025, 3, 27);
        LocalDate endDate = LocalDate.of(2025, 4, 10);
        // 1. Maak een project aan
        Project project = new Project("Avans DevOps Project");

        // 2. Maak teamleden en stel notificaties in
        Developer dev = new Developer("Alice");
        dev.addNotificationMethod(new EmailNotification());
        dev.addNotificationMethod(new SlackNotification());

        ScrumMaster scrumMaster = new ScrumMaster("Bob");
        scrumMaster.addNotificationMethod(new EmailNotification());

        ProductOwner po = new ProductOwner("Charlie");
        po.addNotificationMethod(new SlackNotification());

        project.addTeamMember(dev);
        project.addTeamMember(scrumMaster);
        project.setProductOwner(po);

        // 3. Maak een BacklogItem en voeg het toe aan het project
        BacklogItem backlogItem = new BacklogItem("Implement login feature");
        backlogItem.addObserver(dev); // Notificeer de developer bij wijzigingen
        backlogItem.assignDeveloper(dev); // Assign the developer
        
        // Add activity and mark it as done
        Activity activity = new Activity("Implement OAuth2", 4);
        activity.setDone(true);
        backlogItem.addActivity(activity);

        // Simuleer state-transities in het BacklogItem (bijv. Todo -> Doing)
        System.out.println("Backlog item initial state: " + backlogItem.getState().getName());
        backlogItem.moveToNextState();  // van Todo naar Doing
        System.out.println("Backlog item state after move: " + backlogItem.getState().getName());

        // Move backlog item to done for release
        backlogItem.moveToNextState(); // Doing -> Ready for Testing
        backlogItem.moveToNextState(); // Ready for Testing -> Testing
        backlogItem.moveToNextState(); // Testing -> Tested
        backlogItem.moveToNextState(); // Tested -> Done
        System.out.println("Backlog item final state: " + backlogItem.getState().getName());

        // 4. Maak een Discussion forum voor dit backlog item (Composite pattern)
        DiscussionThread discussionThread = new DiscussionThread("Login Feature Discussion");
        DiscussionMessage message1 = new DiscussionMessage("We moeten OAuth2 implementeren.", scrumMaster);
        DiscussionMessage message2 = new DiscussionMessage("Overweeg ook 2FA.", po);
        discussionThread.add(message1);
        discussionThread.add(message2);
        System.out.println("Discussion thread created with title: " + discussionThread.getContent());

        // 5. SCM stub: Simuleer een commit die gerelateerd is aan het backlog item
        System.out.println("SCM stub: Committing changes for '" + backlogItem.getTitle() + "'.");

        // 6. Maak een ReleaseSprint met een gekoppelde Pipeline
        Pipeline pipeline = new Pipeline("Deployment Pipeline", new FailFastStrategy());
        // Voeg pipeline stappen toe via de Factory (Source, Build, Test, Deploy)
        pipeline.createAndAddStep("source");
        pipeline.createAndAddStep("build");
        pipeline.createAndAddStep("test");
        pipeline.createAndAddStep("deploy");

        ReleaseSprint releaseSprint = new ReleaseSprint("Release Sprint 1", startDate, endDate);
        releaseSprint.setPipeline(pipeline);
        releaseSprint.addBacklogItem(backlogItem);
        releaseSprint.setScrumMaster(scrumMaster);
        releaseSprint.addTeamMember(dev);

        // First start the sprint
        releaseSprint.start();
        System.out.println("Sprint started");
        
        // Then finish the sprint
        releaseSprint.finish();
        System.out.println("Sprint finished");

        // 7. Doorloop de ReleaseSprint states via de API-methoden
        System.out.println("Initial release state: " + releaseSprint.getState().getName());
        releaseSprint.startRelease(); // van ReleaseCreatedState naar ReleaseInProgressState
        System.out.println("After startRelease: " + releaseSprint.getState().getName());

        releaseSprint.performRelease(); // van InProgress naar ReleasingState
        System.out.println("After first performRelease: " + releaseSprint.getState().getName());

        // Simuleer meerdere performRelease calls om door de states te lopen
        releaseSprint.performRelease(); // ReleasingState -> ReleaseFinishedState
        System.out.println("After second performRelease: " + releaseSprint.getState().getName());

        releaseSprint.performRelease(); // ReleaseFinishedState -> ReleasedState
        System.out.println("After third performRelease: " + releaseSprint.getState().getName());

        // Sluit de release af, waardoor de state naar ReleaseClosedState gaat
        releaseSprint.closeRelease();
        System.out.println("After closeRelease: " + releaseSprint.getState().getName());

        // 8. Genereer een rapport voor de sprint met de Report Strategy en Decorator patterns
        IReport report = new PdfReportStrategy().generate(releaseSprint);
        report = new HeaderDecorator(report, "Avans DevOps - Release Report");
        report = new FooterDecorator(report, "Report generated on: " + LocalDate.now());
        System.out.println("Generated Report:\n" + report.getContent());
    }
}
