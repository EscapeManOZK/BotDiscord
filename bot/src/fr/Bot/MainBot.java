package fr.Bot;

import net.dv8tion.jda.client.entities.Group;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.*;

public class MainBot extends ListenerAdapter
{
    private static boolean stop=false;
    private static String id="s!";
    private List<Commande> m_command= new ArrayList<Commande>();
    private List<Serveur> m_serveur = new ArrayList<Serveur>();


    /**
     * This is the method where the program starts.
     */
    public static void main(String[] args)
    {
        JDA jda = null;
        //We construct a builder for a BOT account. If we wanted to use a CLIENT account
        // we would use AccountType.CLIENT
        try
        {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(args[0])           //The token of the account that is logging in.
                    .addEventListener(new MainBot())  //An instance of a class that will handle events.
                    .buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.

        }
        catch (LoginException e)
        {
            //If anything goes wrong in terms of authentication, this is the exception that will represent it
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            //Due to the fact that buildBlocking is a blocking method, one which waits until JDA is fully loaded,
            // the waiting can be interrupted. This is the exception that would fire in that situation.
            //As a note: in this extremely simplified example this will never occur. In fact, this will never occur unless
            // you use buildBlocking in a thread that has the possibility of being interrupted (async thread usage and interrupts)
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

    private void init() {
        Commande help = new Commande("HELP","help","Affiches toutes les commandes disponibles ainsi que la liste de tous les serveurs");
        Commande etat = new Commande("ETAT","etat","affiche l'état de tous les serveurs ( si ils sont online ou offline");
        Commande server = new Commande("SERVER","srv <nom_server>","affiche l'état du serveur spécifier");
        Commande on = new Commande("ONLINE", "online","Affiche les serveurs disponibles");
        Commande off = new Commande("OFFLINE", "offline","Affiche les serveurs non disponible");
        m_command.add(help);
        m_command.add(etat);
        m_command.add(server);
        m_command.add(on);
        m_command.add(off);
    }

    private static void exit(JDA jda) {
        jda.getTextChannelById("429046247798865920").sendTyping();
        jda.getTextChannelById("429046247798865920").sendMessage("Au revoir @everyone").queue();
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
        init();
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
            if (msg.equals(id + "help")) {
                channel.sendTyping();
                if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS)) {
                    channel.sendTyping();
                    channel.sendMessage(setEmbedBuilder_Helper().build()).queue();
                } else {
                    channel.sendTyping();
                    channel.sendMessage("@" + author.getName() + " regarde tes messages privés").queue();
                    author.openPrivateChannel().complete().sendTyping();

                    author.openPrivateChannel().complete().sendMessage(setEmbedBuilder_Helper().build()).queue();
                }
            } else if (msg.equals(id + "roll")) {
                Random rand = new Random();
                int roll = rand.nextInt(6) + 1;
                channel.sendMessage("Your roll: " + roll).queue(sentMessage ->
                {
                    if (roll < 3) {
                        channel.sendMessage("The roll for messageId: " + sentMessage.getId() + " wasn't very good... Must be bad luck!\n").queue();
                    }
                });
            } else if (msg.equals(id + "help")) {

            }
        }else{
            event.getMessage().delete();
            author.openPrivateChannel().complete().sendTyping();
            author.openPrivateChannel().complete().sendMessage(author.getName()+" je ne peux pas vous répondre dans le channel ["+channel.getName()+"] refaite la commande dans ["+jda.getTextChannelById("429046247798865920").getName()+"]").queue();
        }
    }

    private EmbedBuilder setEmbedBuilder_Helper() {
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("HELP");
        build.setDescription("Voici tous les commandes disponible : \n\nPour chaque commande rajouter devant \""+id+"\" \n\n ");

        for (Commande c : m_command) {
            build.appendDescription("[COMMANDE]["+c.getM_title() + "]\n> " + c.getM_command() + "  " + c.getM_descrip()+"\n\n");
        }
        build.appendDescription("==========================================================\n\nVoici tous les serveurs : \n Aucun serveur reconnue actuellement\n Merci de votre patience BotDiscord en cours de developpement");
        return build;
    }
}
