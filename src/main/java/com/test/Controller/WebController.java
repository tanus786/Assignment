package com.test.Controller;

import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.test.entities.CustomerRequest;

@Controller
public class WebController {

	private String bearerToken;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/AddCustomer")
	public String add(Model model) {
		return "add";
	}

	@PostMapping("/authenticate")
	public String authenticate(Model model) {
		String authUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestBody = "{\"login_id\": \"test@sunbasedata.com\", \"password\": \"Test@123\"}";

		ResponseEntity<String> authResponse = restTemplate.postForEntity(authUrl,
				new HttpEntity<>(requestBody, headers), String.class);

		if (authResponse.getStatusCode() == HttpStatus.OK) {
			bearerToken = authResponse.getBody();
			model.addAttribute("message", bearerToken);
		} else {
			model.addAttribute("message", "Authentication failed.");
		}
		return "home";
	}

	@GetMapping("/getCustomerList")
	public String getCustomerList(Model model) {
		String getUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + "dGVzdEBzdW5iYXNlZGF0YS5jb206VGVzdEAxMjM=");

		HttpEntity<String> entity = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(getUrl, HttpMethod.GET, entity, String.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			System.out.println(response.toString());
			model.addAttribute("customerList", response.getBody());
		} else {
			model.addAttribute("customerList", "Failed to fetch customer list.");
		}
		return "home";
	}

	@PostMapping("/addCustomer")
	public String createCustomer(CustomerRequest customerRequest, Model model) {
		String createUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=create";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + "dGVzdEBzdW5iYXNlZGF0YS5jb206VGVzdEAxMjM=");

		HttpEntity<CustomerRequest> requestEntity = new HttpEntity<>(customerRequest, headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(createUrl, HttpMethod.POST, requestEntity,
				String.class);

		if (response.getStatusCode() == HttpStatus.CREATED) {
			model.addAttribute("message", "Customer created successfully.");
		} else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
			model.addAttribute("message", "Failed to create customer. First Name or Last Name is missing.");
		} else {
			model.addAttribute("message", "An error occurred while creating the customer.");
		}
		return "home";
	}
}