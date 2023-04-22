package curso.api.rest.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class ServiceEnviaEmail {

	private String userName = "xgamemaster000@gmail.com";
	private String senha = "jtouqgsecxywvabu";

	public void eviarEmail(String assunto, String emailDestino, String mesagem) throws MessagingException {

		Properties properties = new Properties();
		properties.put("mail.smtp.ssl.trust", "*");
		properties.put("mail.smtp.auth", "true"); // autorização
		properties.put("mail.smtp.starttls", "true"); // auteticacao
		properties.put("mail.smtp.host", "smtp.gmail.com"); // servidor google
		properties.put("mail.smtp.port", "465"); // porta servidor
		properties.put("mail.smtp.socketFactory.port", "465"); // especifica porta socket
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");// classe de coexao socket

		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, senha);
			}

		});

		Address[] toUser = InternetAddress.parse(emailDestino);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(userName)); // quem está enviando
		message.setRecipients(Message.RecipientType.TO, toUser); // para quem vai o email
		message.setSubject(assunto);// assuto do email
		message.setText(mesagem);

		Transport.send(message);

	}

}
