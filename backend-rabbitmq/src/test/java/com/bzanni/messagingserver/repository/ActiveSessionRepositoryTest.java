package com.bzanni.messagingserver.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.bzanni.messagingserver.domain.ActiveSession;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActiveSessionRepositoryTest {

	@Resource
	private IActiveSessionRepository activeSessionRepository;

	@Test
	public void test() throws ActiveSessionRepositoryException {
		ActiveSession read = null;
		String userId = "userId";
		String listeningAddress = "";
		String listeningKey = "";
		ActiveSession sessionToCreate = new ActiveSession(userId, listeningAddress, listeningKey);
		assertFalse(sessionToCreate.isAcked());

		// test valid create
		activeSessionRepository.create(sessionToCreate);

		read = activeSessionRepository.read(userId);
		assertEquals(userId, read.getUserId());
		assertEquals(listeningAddress, read.getListeningAddress());
		assertEquals(listeningKey, read.getListeningKey());
		assertFalse(read.isAcked());

		// test invalid create: already connected user
		boolean exception = false;
		try {
			activeSessionRepository.create(sessionToCreate);
		} catch (ActiveSessionRepositoryException e) {
			exception = true;
		}
		assertTrue(exception);

		// test valid update
		read.setAcked(true);
		activeSessionRepository.update(read);
		read = activeSessionRepository.read(userId);
		assertEquals(userId, read.getUserId());
		assertEquals(listeningAddress, read.getListeningAddress());
		assertEquals(listeningKey, read.getListeningKey());
		assertTrue(read.isAcked());

		// test invalid update: unknown user
		ActiveSession sessionToUpdate = new ActiveSession("XXX", listeningAddress, listeningKey);
		exception = false;
		try {
			activeSessionRepository.update(sessionToUpdate);
		} catch (ActiveSessionRepositoryException e) {
			exception = true;
		}
		assertTrue(exception);

		// test valid delete
		activeSessionRepository.delete(read.getUserId());
		exception = false;
		try {
			activeSessionRepository.read(read.getUserId());
		} catch (ActiveSessionRepositoryException e) {
			exception = true;
		}
		assertTrue(exception);

		// test invalid delete: unknown user
		exception = false;
		try {
			activeSessionRepository.delete(read.getUserId());
		} catch (ActiveSessionRepositoryException e) {
			exception = true;
		}
		assertTrue(exception);
	}
}
