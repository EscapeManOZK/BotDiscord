package fr.Bot;

import net.dv8tion.jda.client.entities.Group;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.*;


public class MainBot extends ListenerAdapter
{
    private static boolean stop=false;
    private GestionCommande GCommand;
    private GestionServer Gserver;
    private boolean GserverInitialise = false;



    /**
     * This is the method where the program starts.
     */
    public static void main(String[] args)
    {

        JDA jda = null;
        try
        {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(args[0])
                    .addEventListener(new MainBot())
                    .buildBlocking();

        }
        catch (LoginException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        while (!stop) {
            Scanner scanner = new Scanner(System.in);
            String cmd = scanner.next();
            if (cmd.equalsIgnoreCase("stop")) {
                exit(jda);
            }
        }
    }

    private void initCommand() {
        GCommand = new GestionCommande();

    }
    private void initServeur()  {
        if (!GserverInitialise) {
            try {
                Gserver = new GestionServer("https://www.g-status.com/game/soulworker");
                GserverInitialise = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                GestionServer Gtemps = new GestionServer("https://www.g-status.com/game/soulworker");
                Gserver.GroupServerActualise(Gtemps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void exit(JDA jda) {
        jda.getTextChannelById("429046247798865920").sendTyping();
        jda.getTextChannelById("429046247798865920").sendMessage("Au revoir everyone").queue();
        jda.shutdown();
        stop = true;
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

        JDA jda = event.getJDA();
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




        if (event.getTextChannel().canTalk()) {

            if (msg.equals(GCommand.getPrefix() + "help")) {
                if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS)) { // si il le bot à les droit pour écrires
                    channel.sendTyping();
                    channel.sendMessage(GCommand.CommandHelp(Gserver).build()).queue();
                } else { // sinon il envoie le message à la personne
                    channel.sendTyping();
                    channel.sendMessage("@" + author.getName() + " regarde tes messages privés").queue();
                    author.openPrivateChannel().complete().sendTyping();
                    author.openPrivateChannel().complete().sendMessage(GCommand.CommandHelp(Gserver).build()).queue();
                }
            }
            if (msg.contains(GCommand.getPrefix() + "srv")){
                if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS)) { // si il le bot à les droit pour écrires
                    channel.sendTyping();
                    channel.sendMessage(GCommand.CommandServer(Gserver , msg.split(" ")[1],msg.split(" ")[2]).build()).queue();
                } else { // sinon il envoie le message à la personne
                    channel.sendTyping();
                    channel.sendMessage("@" + author.getName() + " regarde tes messages privés").queue();
                    author.openPrivateChannel().complete().sendTyping();
                    author.openPrivateChannel().complete().sendMessage(GCommand.CommandHelp(Gserver).build()).queue();
                }
            }
        }else{
            event.getMessage().delete();
            author.openPrivateChannel().complete().sendTyping();
            author.openPrivateChannel().complete().sendMessage(author.getName()+" je ne peux pas vous répondre dans le channel ["+channel.getName()+"] refaite la commande dans ["+jda.getTextChannelById("429046247798865920").getName()+"]").queue();
        }
    }
}
