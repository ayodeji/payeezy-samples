package io.pivotal.payeezy;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/payeezy/cc")
public class CreditCardPaymentsController {

	@RequestMapping("/payeezy/cc/welcome")
	public String welcome(
			@RequestParam(value = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("name", name);
		return "cc-welcome";
	}

	@RequestMapping(value = "/payeezy/cc/cc-welcome", method = RequestMethod.POST  )
	public String makeTransaction(HttpServletRequest request) {
		String firstName = request.getParameter("firstName");
		System.out.println(firstName);
		return "cc-transaction-response";
	}
}
