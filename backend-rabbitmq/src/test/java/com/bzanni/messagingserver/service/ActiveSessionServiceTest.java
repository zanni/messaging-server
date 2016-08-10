package com.bzanni.messagingserver.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import com.bzanni.messagingserver.domain.ActiveSession;
import com.bzanni.messagingserver.repository.ActiveSessionRepository;
import com.bzanni.messagingserver.repository.ActiveSessionRepositoryException;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActiveSessionServiceTest {

	@Resource
	private ActiveSessionService activeSessionService;

	@Resource
	private ActiveSessionRepository activeSessionRepository;

	// @Test
	public void test() throws ActiveSessionServiceException, ActiveSessionRepositoryException {
		String userId = "userId";

		// create user
		ActiveSession create = activeSessionService.create(userId);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ActiveSession read = activeSessionRepository.read(userId);
		assertEquals(userId, read.getUserId());
		assertFalse(read.isAcked());

		assertEquals(create.getUserId(), read.getUserId());
		assertEquals(create.getListeningAddress(), read.getListeningAddress());
		assertEquals(create.getListeningKey(), read.getListeningKey());
		assertEquals(create.isAcked(), read.isAcked());

		activeSessionService.ack(create.getListeningKey());
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		read = activeSessionRepository.read(userId);
		assertTrue(read.isAcked());

	}
}
