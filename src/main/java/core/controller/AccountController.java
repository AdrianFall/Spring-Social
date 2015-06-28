package core.controller;

import core.entity.Account;
import core.entity.VerificationToken;
import core.event.OnRegistrationCompleteEvent;
import core.model.form.LoginForm;
import core.model.form.RegistrationForm;
import core.service.AccountService;
import core.service.EmailService;
import core.service.exception.EmailExistsException;
import core.service.exception.EmailNotSentException;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Adrian on 09/05/2015.
 */
@Controller

public class AccountController {

    @Autowired
    AccountService accountService;
    @Autowired
    MessageSource messages;
    @Autowired
    EmailService emailService;


    public static final String MODEL_REG_FORM = "registrationForm";

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView registrationConfirm(@RequestParam("token") String token, WebRequest request, Model model){
        Locale locale = request.getLocale();
        ModelAndView mav = new ModelAndView();
        VerificationToken verificationToken = accountService.findVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("registration.invalidToken", null, locale);
            mav.addObject("error", message);
            mav.setViewName("redirect:/login");

            return mav;
        }

        Account acc = verificationToken.getAcc();
        Calendar cal = Calendar.getInstance();
        Long timeDiff = verificationToken.getExpiryDate().getTime() - cal.getTime().getTime();
        if (timeDiff <= 0) {
            mav.addObject("error", messages.getMessage("registration.tokenExpired", null, locale));
            // TODO AUTOMATICALLY RESEND THE EMAIL VERIFICATION
            mav.setViewName("redirect:/login");
            return mav;
        }

        // Activate the acc
        acc.setEnabled(true);
        Account updatedAcc = accountService.updateAccount(acc);
        if (updatedAcc == null) {

            mav.addObject("error", messages.getMessage("registration.couldNotBeActivated", null, locale));
            mav.setViewName("redirect:/login");
            return mav;
        }
        mav.addObject("msg", messages.getMessage("registration.activated", null, locale));
        mav.setViewName("redirect:/login");
        return mav;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegistrationForm(Model m) {
        System.out.println("Getting form account.");
        RegistrationForm tempRegForm = new RegistrationForm();
        tempRegForm.setEmail("def@au.lt");
        m.addAttribute("registrationForm",tempRegForm);
        return "registrationForm";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@Valid @ModelAttribute LoginForm loginForm,
                              BindingResult bResult,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam(value = "errorAuthentication", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {
        ModelAndView model = new ModelAndView();
        if (error != null) {

            if (error.equals("badCredentials")) {
                bResult.rejectValue("email", "login.badCredentials");
            }
            else if (error.equals("credentialsExpired"))
                bResult.rejectValue("password", "login.credentialsExpired");

            else if (error.equals("accountExpired"))
                bResult.rejectValue("email", "login.accountExpired");
            else if (error.equals("accountLocked"))
                bResult.rejectValue("email", "login.accountLocked");
            else if (error.equals("accountDisabled")) {
                bResult.rejectValue("email", "login.accountDisabled");
            }

            // And now cancel the parameter from the request URL
        }

        model.setViewName("login");

        if (logout != null) {
            model.addObject("msg", "Logging out out.");
            model.setViewName("logout");
        }


        return model;
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView createAccount(@Valid @ModelAttribute RegistrationForm regForm,
                                      BindingResult bResult, HttpServletRequest request, Model m) {

        // Validate the form password & passConfirm match
        if (!regForm.getPassword().equals(regForm.getConfirmPassword())) {
            bResult.rejectValue("password", "registration.passwordMismatch");
            bResult.rejectValue("confirmPassword", "registration.passwordMismatch");
            System.out.println("Password match validation - failed.");
        }

        if (bResult.hasErrors()) {
            // Clean the password fields before returning the form again
            regForm.setPassword("");
            regForm.setConfirmPassword("");

            return new ModelAndView("registrationForm");
        }

        // Build up the Account entity (to be used for createAcc(arg))
        Account newAcc = new Account();
        newAcc.setEmail(regForm.getEmail());
        newAcc.setPassword(regForm.getPassword());


        try {// Attempt acc creation
            Account acc = accountService.createAccount(newAcc);
            if (acc != null) {
                ModelAndView mav = new ModelAndView();
                // Acc created
                String appUrl = request.getContextPath();

                emailService.sendConfirmationEmail(new OnRegistrationCompleteEvent(newAcc, request.getLocale(), appUrl));
                // Forward to the login, acknowleding that account has been created
                mav.addObject("msg", messages.getMessage("registration.created", null, request.getLocale()));
                mav.setViewName("redirect:/login");
                return mav;
            } else { // Acc not created
                bResult.rejectValue("email", "registration.accCreationError");
                // Clean the passwords fields
                regForm.setPassword("");
                regForm.setConfirmPassword("");
                return new ModelAndView("registrationForm");
            }
        }  catch(EmailExistsException eee) {
            System.out.println("Catched EmailexistsException, attaching the rejected value to BindingREsult");
            bResult.rejectValue("email", "registration.emailExists");
            // Clean the password fields before returning the form again
            regForm.setPassword("");
            regForm.setConfirmPassword("");
            return new ModelAndView("registrationForm");
        } catch (EmailNotSentException uee) {
            System.out.println("Catched EmailNotSentException, attaching the rejected value to BindingResult");
            bResult.rejectValue("email", "registration.emailCouldNotBeSent");
            return new ModelAndView("registrationForm");
        }
       /* Account createdAcc = accountService.createAccount(newAcc);
        if (createdAcc == null) {
            bResult.rejectValue("username", "usernameExists");
            System.out.println("Account not created.");
        } else {
            System.out.println("Registered account. with id: " + createdAcc.getId());
        }
        // Clean the passwords from the regForm
        regForm.setPassword(null);
        //m.addAttribute("message", "Registered account for: " + regForm.toString());*/

    }
}
