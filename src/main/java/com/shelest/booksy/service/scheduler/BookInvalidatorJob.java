package com.shelest.booksy.service.scheduler;

import com.shelest.booksy.domain.model.Book;
import com.shelest.booksy.domain.model.enumeration.BookStatus;
import com.shelest.booksy.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookInvalidatorJob {

    private final BookRepository bookRepository;

    @Value("${booksy.jobs.book-cleanup-threshold-days}")
    private int thresholdDays;

    @Scheduled(cron = "${booksy.jobs.book-cleanup-cron}")
    public void markOldBooksAsToBeRemoved() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(thresholdDays);

        List<Book> oldBooks = bookRepository.findByCreatedAtBeforeAndStatus(thresholdDate, BookStatus.ACTIVE);

        if (!oldBooks.isEmpty()) {
            log.info("Found {} old books to mark as TO_BE_REMOVED (older than {} days)", oldBooks.size(), thresholdDays);
            oldBooks.forEach(book -> book.setStatus(BookStatus.TO_BE_REMOVED));
            bookRepository.saveAll(oldBooks);
        } else {
            log.info("No old books to update today.");
        }
    }
}
