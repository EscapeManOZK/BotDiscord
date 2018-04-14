package fr.Bot;

import net.dv8tion.jda.client.entities.Group;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.RestAction;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class MainBot extends ListenerAdapter
{
    private static boolean stop=false;
    private GestionCommande GCommand;
    private GestionServer Gserver;
    private boolean GserverInitialise = false;
    private TextChannel Information;
    JDA jda ;



    /**
     * This is the method where the program starts.
     */
    public static void main(String[] args)
    {
        MainBot run = new MainBot();
        run.run(args);
    }

    private void initCommand() {
        GCommand = new GestionCommande();

    }

    private void initServeur()  {
        try {
            Gserver = new GestionServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void onChangeServer() {
        //clean channel info
        try {
            GestionServer Gtemps = new GestionServer();
            if (Gserver.GroupServerActualise(Gtemps)){
                Information.sendMessage(GCommand.ChangeServeur(Gserver).build()).queue();
            }else {
                String message = "Les serveurs n'ont changer pas d'état";
                if (Information.getLatestMessageId()!=null) {
                    Message msg = Information.getMessageById(Information.getLatestMessageId()).complete();
                    System.out.println(msg.toString());
                    if (msg.getContentDisplay().contains(message)) {
                        System.out.println("passer");
                        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat h = new SimpleDateFormat("hh:mm");

                        Date currentTime_1 = new Date();

                        String dateString = d.format(currentTime_1);
                        String heureString = h.format(currentTime_1);
                        msg.editMessage(message + " [ " + heureString + " , " + dateString + " ]").queue();
                    }else
                        sendMessageNoChange(message);

                }else{
                    sendMessageNoChange(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageNoChange(String message) {
        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat h = new SimpleDateFormat("hh:mm");

        Date currentTime_1 = new Date();

        String dateString = d.format(currentTime_1);
        String heureString = h.format(currentTime_1);
        Information.sendMessage(message + " [ " + heureString + " , " + dateString + " ]").queue();
    }

    private void exit() {
        Information.sendTyping();
        Information.sendMessage("Bye everyone").queue();
        //Information.sendMessage(GCommand.CommandEtat(Gserver).build()).queue();
        jda.shutdown();
        stop = true;
    }

    private void run(String[] args) {
        initCommand();
        initServeur();
        try
        {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(args[0])
                    .addEventListener(new MainBot())
                    .buildBlocking();
            Information = jda.getTextChannelById("434462918990495761");
            Information.sendTyping();
            Information.sendMessage("Hello everyone").queue();
            Information.sendMessage(GCommand.CommandEtat(Gserver).build()).queue();

        }
        catch (LoginException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        Timer timer;
        timer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                onChangeServer();
            }
        });
        timer.start();
        while (!stop) {
            Scanner scanner = new Scanner(System.in);
            String cmd = scanner.next();
            if (cmd.equalsIgnoreCase("stop")) {
                exit();
            }
        }
    }

    /**
     * NOTE THE @Override!
     * This method is actually overriding a method in the ListenerAdapter class! We place an @Override annotation
     *  right before any method that is overriding another to guarantee to ourselves that it is actually overriding
     *  a method from a super class properly. You should do this every time you override a method!
     *
     * As stated above, this method is overriding a hook method in the
     * {@link net.dv8tion.jda.core.hooks.ListenerAdapter ListenerAdapter} class. It has convience methods for all JDA events!
     * Consider looking through the events it offers if you plan to use the ListenerAdapter.
     *
     * In this example, when a message is received it is printed to the console.
     *
     * @param event
     *          An event containing information about a {@link net.dv8tion.jda.core.entities.Message Message} that was
     *          sent in a channel.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        initCommand();
        initServeur();
        JDA jda =event.getJDA();
        User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String msg = message.getContentDisplay();

        boolean bot = author.isBot();

        if (event.isFromType(ChannelType.TEXT))
        {
            Guild guild = event.getGuild();
            TextChannel textChannel = event.getTextChannel();
            Member member = event.getMember();

            String name;
            if (message.isWebhookMessage())
            {
                name = author.getName();
            }
            else
            {
                name = member.getEffectiveName();
            }

            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
        }
        else if (event.isFromType(ChannelType.PRIVATE))
        {
            PrivateChannel privateChannel = event.getPrivateChannel();

            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
        }
        else if (event.isFromType(ChannelType.GROUP))
        {
            Group group = event.getGroup();
            String groupName = group.getName() != null ? group.getName() : "";

            System.out.printf("[GRP: %s]<%s>: %s\n", groupName, author.getName(), msg);
        }



        int i;
        boolean find=false;
        for (i=0;i<GCommand.getM_command().size()&&!find;i++) {
            if(msg.contains(GCommand.getPrefix()+GCommand.getM_command().get(i).getM_command())){
                find=true;
            }
        }
        i--;
        if (find){
            if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS)&&event.getTextChannel().getId()=="429046247798865920") { // si il le bot à les droit pour écrires
                channel.sendTyping();
                switch (i){
                    case 0:    //help
                        channel.sendMessage(GCommand.CommandHelp(Gserver).build()).queue();
                        break;
                    case 1:    //état
                        channel.sendMessage(GCommand.CommandEtat(Gserver).build()).queue();
                        break;
                    case 2:    //server
                        channel.sendMessage(GCommand.CommandServer(Gserver , msg).build()).queue();
                        System.out.println("ok");
                        break;
                    case 3:    //online
                        channel.sendMessage(GCommand.CommandOn(Gserver).build()).queue();
                        break;
                    case 4:    //offline
                        channel.sendMessage(GCommand.CommandOff(Gserver).build()).queue();
                        break;
                }


            } else { // sinon il envoie le message à la personne
                channel.sendTyping();
                event.getMessage().delete().reason("Pas au bon channel").queue();
                author.openPrivateChannel().complete().sendTyping();
                switch (i){
                    case 0:    //help
                        author.openPrivateChannel().complete().sendMessage(GCommand.CommandHelp(Gserver).build()).queue();
                        break;
                    case 1:    //état
                        author.openPrivateChannel().complete().sendMessage(GCommand.CommandEtat(Gserver).build()).queue();
                        break;
                    case 2:    //server
                        author.openPrivateChannel().complete().sendMessage(GCommand.CommandServer(Gserver , msg).build()).queue();
                        System.out.println("ok");
                        break;
                    case 3:    //online
                        author.openPrivateChannel().complete().sendMessage(GCommand.CommandOn(Gserver).build()).queue();
                        break;
                    case 4:    //offline
                        author.openPrivateChannel().complete().sendMessage(GCommand.CommandOff(Gserver).build()).queue();
                        break;
                }
            }

        }
    }
}
