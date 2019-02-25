
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.ProcessionService;
import domain.Actor;
import domain.Procession;

@Controller
@RequestMapping("/procession")
public class ProcessionController extends AbstractController {

	// Services

	@Autowired
	private ProcessionService	processionService;

	@Autowired
	private ActorService actorService;

	// Display
	
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam int processionId) {

		ModelAndView result;
		Procession procession;
		boolean isPrincipal = false;
		Actor principal;
		
		principal = this.actorService.findByPrincipal();
		procession = this.processionService.findOne(processionId);
		
		if(procession.getBrotherhood().getId() == principal.getId())
			isPrincipal = true;
		
		result = new ModelAndView("procession/display");
		result.addObject("procession", procession);
		result.addObject("isPrincipal", isPrincipal);
		result.addObject("requestURI", "procession/display.do?processionId=" + processionId);

		return result;
	}
	
	//List

	@RequestMapping(value = "/member,brotherhood/list")
	public ModelAndView list(@RequestParam(required = false) Integer memberId, @RequestParam(required = false) Integer brotherhoodId) {
		ModelAndView result;
		Collection<Procession> processions;
		Actor principal;
		
		principal = this.actorService.findByPrincipal();

		if (this.actorService.checkAuthority(principal, "BROTHERHOOD")) {

			processions = this.processionService.findProcessionsByBrotherhoodId(principal.getId());
			
			String requestURI = "procession/member,brotherhood/list.do?brotherhoodId=" + principal.getId();
			result = new ModelAndView("procession/list");
			result.addObject("requestURI", requestURI);
			result.addObject("processions", processions);

			return result;

		} else {

			processions = this.processionService.findProcessionsByMemberId(principal.getId());
			
			String requestURI = "procession/member,brotherhood/list.do?memberId=" + principal.getId();
			result = new ModelAndView("procession/list");
			result.addObject("requestURI", requestURI);
			result.addObject("processions", processions);

			return result;
		}
	}
	
	// Creation 
	
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		final ModelAndView result;
		Procession procession;

		procession = this.processionService.create();

		result = this.createEditModelAndView(procession);

		return result;

	}

	// Edition
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int processionId) {
		ModelAndView result;
		Procession procession;

		procession = this.processionService.findOne(processionId);
		Assert.notNull(procession);
		result = this.createEditModelAndView(procession);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Procession procession, final BindingResult binding) {
		ModelAndView result;
		
		procession = processionService.reconstruct(procession, binding);
		if (binding.hasErrors())
			result = this.createEditModelAndView(procession);
		else
			try {
				this.processionService.save(procession);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(procession, "procession.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Procession procession, final BindingResult binding) {
		ModelAndView result;

		try {
			this.processionService.delete(procession);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(procession, "procession.commit.error");
		}

		return result;
	}

	//Ancillary methods
	protected ModelAndView createEditModelAndView(final Procession procession) {
		ModelAndView result;

		result = this.createEditModelAndView(procession, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Procession procession, final String messageCode) {
		final ModelAndView result;
		Actor principal;
		boolean isBrotherhood = false;
		boolean isPrincipal = false;
		
		principal = this.actorService.findByPrincipal();
		Assert.isTrue(this.actorService.checkAuthority(principal, "BROTHERHOOD"), "not.allowed");
		
		if(this.actorService.checkAuthority(principal, "BROTHERHOOD")) {
			isBrotherhood = true;
		}
		
		if(procession.getId() != 0 && procession.getBrotherhood().getId() == principal.getId())
			isPrincipal = true;
				
		result = new ModelAndView("procession/edit");
		result.addObject("procession", procession);
		result.addObject("isBrotherhood", isBrotherhood);
		result.addObject("isPrincipal", isPrincipal);
		result.addObject("message", messageCode);

		return result;

	}

}