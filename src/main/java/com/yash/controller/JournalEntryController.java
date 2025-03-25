package com.yash.controller;

import com.yash.entity.JournalEntry;
import com.yash.entity.User;
import com.yash.service.JournalEntryService;
import com.yash.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String username ) {
        User user = userService.findByUserName(username);
        List<JournalEntry> all = user.getJournalEntries();
        if(all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all , HttpStatus.OK) ;
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{username}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry , @PathVariable String username) {
        try {
            journalEntryService.saveEntry(myEntry , username);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{my_id}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable ObjectId my_id) {
        Optional<JournalEntry> journalEntry = journalEntryService.findById(my_id);
        if (journalEntry.isPresent()) {
            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/id/{username}/{my_id}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId my_id , @PathVariable String username) {
        journalEntryService.deleteById(my_id , username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/id/{username}/{my_id}")
    public ResponseEntity<?> updateEntryById(@PathVariable ObjectId my_id,
                                             @RequestBody JournalEntry newEntry,
                                             @RequestParam String username) {
        JournalEntry oldEntry = journalEntryService.findById(my_id).orElse(null);
        User user = userService.findByUserName(username);

        if (oldEntry != null) {
            if (newEntry.getTitle() != null && !newEntry.getTitle().isEmpty()) {
                oldEntry.setTitle(newEntry.getTitle());
            }
            if (newEntry.getContent() != null && !newEntry.getContent().isEmpty()) {
                oldEntry.setContent(newEntry.getContent());
            }
            journalEntryService.saveEntry(oldEntry);
            return new ResponseEntity<>(oldEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
