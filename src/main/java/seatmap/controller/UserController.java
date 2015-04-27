package seatmap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import seatmap.dto.UserDto;
import seatmap.entity.Participant;
import seatmap.repository.ParticipantRepository;

@Controller
public class UserController {
	private ParticipantRepository participantRepository;

	@Autowired
	public UserController(ParticipantRepository participantRepository) {
		this.participantRepository = participantRepository;
	}

	@RequestMapping(value = "/saveUser", method = RequestMethod.POST)
	public String saveUser(UserDto userDto, Model model) {
		final Participant p = new Participant();
		p.setName(userDto.getName());
		participantRepository.save(p);
		return "redirect:/";
	}

	@RequestMapping(value = "/removeUser/{id}", method = RequestMethod.GET)
	public String deleteUser(@PathVariable Long id, Model model) {
		participantRepository.delete(id);
		return "redirect:/";
	}
}
