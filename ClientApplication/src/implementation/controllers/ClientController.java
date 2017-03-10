/**
 * Copyright 2017
 *
 *
 * Sous licence Apache, Version 2.0 (la "Licence");
 * Vous ne pouvez pas utiliser ce fichier sauf en conformité avec la licence.
 * Vous pouvez obtenir une copie de la licence à l'adresse :
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Sauf si requis par la loi en vigueur ou accord écrit, le logiciel
 * Distribué sous licence est distribué «TEL QUEL»,
 * SANS GARANTIES OU CONDITIONS D'AUCUNE SORTE, express ou implicite.
 * Voir la licence pour les autorisations spécifiques aux différentes langues et
 * Limitations sous la licence.
 *
 * Contribué par : Jesus GARNICA OLARRA, Hivinau GRAFFE
 */

package implementation.controllers;

import client.*;
import helpers.*;
import java.util.*;
import javax.swing.*;
import client.event.*;
import java.util.prefs.*;
import common.annotations.*;
import common.serializable.*;
import java.util.concurrent.*;
import implementation.global.*;

@Developer(name="Jesus GARNICA OLARRA")
public class ClientController extends AbstractClientObserver {
	
	private final PrivateChatController privateChatController;
	private final PublicChatController publicChatController;
	private final ExecutorService executor; 
	private final List<Future<?>> threads;
	
	private Client client = null;
	
	public ClientController(JTabbedPane tab) {
		
		privateChatController = new PrivateChatController();
		publicChatController = new PublicChatController(this);
		
        tab.addTab(Resource.getInstance().getString("private_section"), privateChatController);
        tab.addTab(Resource.getInstance().getString("public_section"), publicChatController);
		
		executor = new ThreadPoolExecutor(5, 
				8, 
				60, TimeUnit.SECONDS, 
				new LinkedBlockingQueue<Runnable>());
		
		threads = Collections.synchronizedList(new LinkedList<>());
        
        start();
	}
	
	@Override
	public void errorOccured(Throwable error) {
		
		error.printStackTrace();
	}
	
	@Override
	public void statusChanged(boolean status) {

		privateChatController.changeConnexionState(status);
	}
	
	@Override
	public User getUser() {

		Preferences preferences = Preferences.userRoot();
		
        final String name = preferences.get(Constant.USERNAME, null);
		return new User(name);
	}
	
	public synchronized void start()  {
		
		Preferences preferences = Preferences.userRoot();

        final String host = preferences.get(Constant.HOSTNAME, null);
        final int port = preferences.getInt(Constant.HOSTPORT, -1);
        
        if(host != null && port != -1) {

        	new Thread(new Runnable() {
				
				@Override
				public void run() {
			        
			        while(client == null) {

			            try {
			        		
			        		client = new Client(host, port);
			        		client.registerObserver(ClientController.this);
			        		
							Future<?> thread = executor.submit(client);
			    			threads.add(thread);
			    			
			    		} catch (Exception ignored) {}
			        }
				}
				
			}).start();
        }
	}
	
	public synchronized void stop() {

		if(client != null) {
			
			client.yield();
		}
		
		for (Iterator<Future<?>> iterator = threads.iterator(); iterator.hasNext();) {
			
			Future<?> thread = iterator.next();

			thread.cancel(true);
			
			iterator.remove();
		}

		/*
		executor.shutdown();
		
		try {
			
			executor.awaitTermination(5, TimeUnit.SECONDS);
			
		} catch (InterruptedException ignored) {}
		
		*/
	}

	public synchronized void restart() {
		
		stop();
		
		client = null;
		
		start();
	}

}
