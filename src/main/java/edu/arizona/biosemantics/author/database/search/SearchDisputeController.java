package edu.arizona.biosemantics.author.database.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import edu.arizona.biosemantics.author.database.search.DisputeRepository;


@RestController
@RequestMapping("/dispute")

public class SearchDisputeController {
	@Autowired
	private DisputeRepository disputeRepo;
	

    @GetMapping("/all")
    public Iterable<Dispute> allDisputes() {
        return disputeRepo.findAll();
    }
}

