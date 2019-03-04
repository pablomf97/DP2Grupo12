package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.MessageRepository;
import domain.Actor;
import domain.Message;
import domain.MessageBox;

@Service
@Transactional
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private ActorService actorService;

	@Autowired
	private Validator validator;

	@Autowired
	private MessageBoxService messageBoxService;


	//CRUD Methods	-----------------------------------------------------------
	public Message create(){

		final Message result;
		Actor principal;
		Collection<MessageBox> boxes = new ArrayList<MessageBox>();

		principal = this.actorService.findByPrincipal();
		Assert.notNull(principal);

		result = new Message();

		result.setSender(principal);
		result.setMessageBoxes(boxes);
		result.setIsSpam(false);

		return result;

	}

	public Message save(Message message){
		Message result;
		Actor principal;
		Date sentMoment;
		String tags;
		Collection<MessageBox> messageBoxes = new ArrayList<MessageBox>();
		MessageBox inSpamBox,outBox;


		//TODO: Check spam

		principal = this.actorService.findByPrincipal();
		Assert.notNull(principal);

		Assert.notNull(message);
		Assert.isTrue(message.getId()==0);
		
		tags = message.getTags();

		sentMoment = new Date(System.currentTimeMillis()-1);

		if(tags.contains("spam")){
			inSpamBox = this.messageBoxService.findByName(message.getRecipient().getId(), "Spam box");
			Assert.notNull(inSpamBox);

			message.setIsSpam(true);
			principal.setSpammer(true);

		}else{
			inSpamBox = this.messageBoxService.findByName(message.getRecipient().getId(), "In box");
			Assert.notNull(inSpamBox);

		}

		outBox = this.messageBoxService.findByName(principal.getId(), "Out Box");
		Assert.notNull(outBox);

		messageBoxes.add(inSpamBox);
		messageBoxes.add(outBox);

		message.setSentMoment(sentMoment);
		message.setMessageBoxes(messageBoxes);

		result  = this.messageRepository.save(message);

		outBox.getMessages().add(result);
		inSpamBox.getMessages().add(result);

		return result;
	}

	public void delete(Message message){
		Actor principal;
		MessageBox trashBoxPrincipal;
		Collection<Message> messagesTrashBox;

		principal = this.actorService.findByPrincipal();
		Assert.notNull(principal);
		
		Assert.notNull(message);
		Assert.isTrue(message.getId()!=0);
		
		trashBoxPrincipal = this.messageBoxService.findByName(principal.getId(), "Trash box");
		Assert.notNull(trashBoxPrincipal);

		messagesTrashBox = trashBoxPrincipal.getMessages();
		Assert.notNull(messagesTrashBox);

		if(messagesTrashBox.contains(message)){
			if(this.findMessage(message)){
				messagesTrashBox.remove(message);

			}else{
				messagesTrashBox.remove(message);
				this.messageRepository.delete(message);

			}
		}else{
			this.move(message,trashBoxPrincipal);
		}


	}
	
	public Message findOne(int messageId){
		Message result;
		
		result = this.messageRepository.findOne(messageId);
		Assert.notNull(result);
		
		return result;
	}
	
	
	public Collection<Message> findAll(){
		Collection<Message> result;
		
		result = this.messageRepository.findAll();
		Assert.notNull(result);
		
		return result;
	}
	
	//Other business methods --------------------------------------------------

	public boolean findMessage(Message message){
		boolean result = false;
		Actor sender;
		Actor recipient;
		Collection<MessageBox>messageBoxSender,messageBoxRecipient;

		messageBoxRecipient = new ArrayList<MessageBox>();
		messageBoxSender = new ArrayList<MessageBox>();

		sender = message.getSender();
		Assert.notNull(sender);

		recipient = message.getRecipient();
		Assert.notNull(recipient);

		messageBoxRecipient = this.messageBoxService.findByOwner(recipient.getId());
		Assert.notNull(messageBoxRecipient);

		messageBoxSender = this.messageBoxService.findByOwner(sender.getId());
		Assert.notNull(messageBoxSender);

		for(MessageBox mb : messageBoxSender){
			if(!mb.getName().equals("Trash Box")){
				if(mb.getMessages().contains(message)){
					result = true;
				}
			}
		}

		for(MessageBox mb : messageBoxRecipient){
			if(mb.getMessages().contains(message)){
				result = true;
			}
		}
		return result;
	}
	
	public void move(final Message message, final MessageBox destination){
		Actor principal;
		MessageBox origin = null;
		Collection<MessageBox> principalBoxes, messageBoxes;
		Collection<Message> messages,updatedOriginBox,updatedDestinationBox;
		
		principalBoxes = new ArrayList<MessageBox>();
		messageBoxes = new ArrayList<MessageBox>();
		messages = new ArrayList<Message>();
		updatedOriginBox = new ArrayList<Message>();
		updatedDestinationBox = new ArrayList<Message>();
		
		principal = this.actorService.findByPrincipal();
		Assert.notNull(principal);
		
		Assert.isTrue(message.getId()!=0);
		Assert.isTrue(destination.getId()!=0);
		
		messageBoxes = message.getMessageBoxes();
		Assert.notNull(messageBoxes);
		
		principalBoxes = this.messageBoxService.findByOwner(principal.getId());
		
		for(MessageBox mb : principalBoxes){
			messages.addAll(mb.getMessages());
		}
		
		Assert.isTrue(messages.contains(message));
		
		for(MessageBox principalBox : principalBoxes){
			if(messages.contains(message)){
				origin = principalBox;
				messageBoxes.remove(origin);
				break;
			}
		}
		
		messageBoxes.add(destination);
		
		Assert.isTrue(principalBoxes.contains(origin));
		Assert.isTrue(principalBoxes.contains(destination));
		
		message.setMessageBoxes(messageBoxes);
		
		updatedOriginBox = origin.getMessages();
		updatedDestinationBox = destination.getMessages();
		
		updatedOriginBox.remove(message);
		updatedDestinationBox.add(message);
		
		origin.setMessages(updatedOriginBox);
		destination.setMessages(updatedDestinationBox);
		
	}
	
	public void broadcast(final Message m){
		Actor principal;
		String subject,priority,body,tags;
		Date sentMoment;
		boolean isSpam;
		MessageBox outBoxAdmin;
		Collection<MessageBox> notificationBoxes ,boxes;
		Message saved;
		
		boxes = new ArrayList<MessageBox>();
		notificationBoxes = new ArrayList<MessageBox>();
		
		principal = this.actorService.findByPrincipal();
		Assert.notNull(principal);
		Assert.isTrue(this.actorService.checkAuthority(principal, "ADMINISTRATOR"));
		
		Assert.notNull(m);
		
		subject = m.getSubject();
		body = m.getBody();
		priority = m.getPriority();
		tags = m.getTags();
		isSpam = false;
		sentMoment = new Date(System.currentTimeMillis()-1);
		
		outBoxAdmin = this.messageBoxService.findByName(principal.getId(), "Out box");
		Assert.notNull(outBoxAdmin);
		
		//notificationBoxes = this.messageBoxService.findAllNotificationExceptAdmin();
		
		final Message message = new Message();
		message.setSubject(subject);
		message.setBody(body);
		message.setSentMoment(sentMoment);
		message.setPriority(priority);
		message.setTags(tags);
		message.setIsSpam(isSpam);
		message.setRecipient(principal);
		message.setSender(principal);
		
		boxes.add(outBoxAdmin);
		boxes.addAll(notificationBoxes);
		
		message.setMessageBoxes(boxes);
		
		saved = this.messageRepository.save(message);
		
		for(MessageBox notBox : notificationBoxes){
			notBox.getMessages().add(saved);
		}
		
		outBoxAdmin.getMessages().add(saved);
		
	}
	
	public void deleteMessages(final Message m, final MessageBox mb){
		Collection<Message> messages= new ArrayList<Message>();
		
		mb.setMessages(messages);
	
		
	}
	
	public Message reconstruct(final Message message, final BindingResult binding){
		Message result = this.create();
		
		if(message.getId()==0){
			result.setSubject(message.getSubject());
			result.setBody(message.getBody());
			result.setPriority(message.getPriority());
			result.setSentMoment(message.getSentMoment());
			result.setTags(message.getTags());
			this.validator.validate(result,binding);
			
		}else{
			final Message m = this.messageRepository.findOne(message.getId());
			result.setSubject(message.getSubject());
			result.setBody(message.getBody());
			result.setPriority(message.getPriority());
			result.setSentMoment(message.getSentMoment());
			result.setTags(message.getTags());
			this.validator.validate(result,binding);
			
			if(!binding.hasErrors()){
				m.setSubject(message.getSubject());
				m.setBody(message.getBody());
				m.setPriority(message.getPriority());
				m.setSentMoment(message.getSentMoment());
				m.setTags(message.getTags());
				
				result = m;
			}
		}
		return result;
	}

}