package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;

public class MainPresenterUnitTest {

    private MainPresenter.View mockView;
    private StatusService mockStatusService;

    private MainPresenter mainPresenterSpy;

    private final String fakePost = "Fake Post";

    @BeforeEach
    public void setup() {
        //Create mocks
        mockView = Mockito.mock(MainPresenter.View.class);
        mockStatusService = Mockito.mock(StatusService.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));

        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);
    }

    private void checkStatus(Answer<Void> answer, MainPresenter mainPresenterSpy, MainPresenter.View mockView) {
        Mockito.doAnswer(answer).when(mockStatusService).post(Mockito.any(), Mockito.any(), Mockito.any());
        mainPresenterSpy.post(fakePost);
        Mockito.verify(mockView).displayMessage("Posting Status...");
    }

    @Test
    public void testPost_postSuccess() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.StatusObserver observer = invocation.getArgument(2, StatusService.StatusObserver.class);
                observer.postSuccess("Successfully Posted!");
                return null;
            }
        };

        checkStatus(answer, mainPresenterSpy, mockView);

        Mockito.verify(mockView).postSuccess("Successfully Posted!");
    }

    @Test
    public void testPost_postFailedWithError() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.StatusObserver observer = invocation.getArgument(2, StatusService.StatusObserver.class);
                observer.displayError("error message");
                return null;
            }
        };

        checkStatus(answer, mainPresenterSpy, mockView);

        Mockito.verify(mockView).displayMessage("error message");
    }

    @Test
    public void testPost_postFailedWithException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.StatusObserver observer = invocation.getArgument(2, StatusService.StatusObserver.class);
                observer.displayException(new Exception("exception error message"));
                return null;
            }
        };

        checkStatus(answer, mainPresenterSpy, mockView);

        Mockito.verify(mockView).displayMessage("exception error message");
    }
}
