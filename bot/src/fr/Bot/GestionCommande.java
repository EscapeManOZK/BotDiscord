package fr.Bot;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.w3c.dom.events.Event;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GestionCommande {

    private static String Prefix="s!";

    private List<Commande> m_command= new ArrayList<Commande>();

    public GestionCommande(){
        Commande help = new Commande("HELP","help","Affiches toutes les commandes disponibles ainsi que la liste de tous les serveurs");
        Commande etat = new Commande("ETAT","etat","affiche l'état de tous les serveurs ( si ils sont online ou offline");
        Commande server = new Commande("SERVER","srv","**<nom_Groupe_de_Serveur> <Initial_de_la_Langue_du_Serveur>**  affiche l'état du serveur spécifier");
        Commande on = new Commande("ONLINE", "online","Affiche les serveurs disponibles");
        Commande off = new Commande("OFFLINE", "offline","Affiche les serveurs non disponible");
        Commande clean = new Commande("CLEAN","clean","<Nb de message> Supprime le nombre indiqué de messages dans le channel ");
        m_command.add(help);
        m_command.add(etat);
        m_command.add(server);
        m_command.add(on);
        m_command.add(off);
        m_command.add(clean);
    }

    public List<Commande> getM_command() {
        return m_command;
    }

    public Commande takeCommandById(int id){
        return m_command.get(id);
    }

    public static String getPrefix() {
        return Prefix;
    }


    public EmbedBuilder CommandHelp(GestionServer Gsrv){
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("**HELP**");
        build.setColor(Color.red);
        build.setDescription("Voici tous les commandes disponible : \n\nPour chaque commande rajouter devant \""+Prefix+"\" \n\n ");

        for (Commande c : m_command) {
            build.appendDescription("[COMMANDE]["+c.getM_title() + "]\n> **" + c.getM_command() + "**  " + c.getM_descrip()+"\n\n");
        }
        build.appendDescription("==========================================================\n\nVoici tous les serveurs : \n ");
        for (int j=0;j<Gsrv.getSize();j++) {
            GroupServer s = Gsrv.takeGroupServerById(j);
            build.appendDescription("\n[SERVER GROUP : "+s.getM_title()+"]\n\n");
            for(int i=0;i<s.getSize();i++){
                Serveur srv = s.getServeurbyId(i);
                build.appendDescription("["+srv.getM_pays()+"]"+srv.getM_name()+"\n");
            }
        }
        return build;
    }

    public EmbedBuilder CommandEtat(GestionServer Gsrv){
        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat h = new SimpleDateFormat("hh:mm");

        Date currentTime_1 = new Date();

        String dateString = d.format(currentTime_1);
        String heureString = h.format(currentTime_1);
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("**ETAT  [ " + heureString + " , " + dateString + " ]**");
        build.setColor(Color.red);
        build.appendDescription("\nVoici tous les serveurs : \n ");
        seeServerData(Gsrv, build);
        return build;
    }

    public EmbedBuilder CommandServer(GestionServer Gsrv, String msg){
        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat h = new SimpleDateFormat("hh:mm");

        Date currentTime_1 = new Date();

        String dateString = d.format(currentTime_1);
        String heureString = h.format(currentTime_1);
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("**SERVEUR  [ " + heureString + " , " + dateString + " ]**");
        build.setColor(Color.red);
        if (!(msg.split(" ").length<3&&msg.split(" ").length>4)) {
            String group="";
            String srv="";
            if (msg.split(" ").length==3) {
                group = msg.split(" ")[1];
                srv = msg.split(" ")[2];
            }else {
                group = msg.split(" ")[1]+" "+msg.split(" ")[2];
                srv = msg.split(" ")[3];
            }
            if (Gsrv.contains(group, srv)) {
                Serveur serv = Gsrv.getGroupeServerByName(group).getServeurbyName(srv);
                String actif = "";
                if (serv.getM_actif()) actif = "ONLINE";
                else actif = "OFFLINE";
                build.appendDescription("[" + serv.getM_pays() + "]" + serv.getM_name() + "   " + serv.getM_date() + "   " + actif);
            } else {
                build.appendDescription("__**/!\\ERREUR/!\\**__ \n");
                if (Gsrv.getGroupeServerByName(group) == null) {
                    build.appendDescription("Veuillez indiquer un nom de Groupe de serveur correcte \nExemple :" +
                            Gsrv.takeGroupServerById(1).getM_title()
                            + "\nPour plus d'information faite la commande **s!help**");
                } else if (Gsrv.getGroupeServerByName(group).getServeurbyName(srv) == null) {
                    if (!srv.contains("[") && srv.contains("]")) {
                        build.appendDescription("Veuillez indiquer le Pays du server \nExemple :" +
                                Gsrv.takeGroupServerById(1).getServeurbyId(3).getM_pays());
                    } else {
                        build.appendDescription("Veuillez indiquer un nom de server correcte \nExemple :" +
                                Gsrv.takeGroupServerById(1).getServeurbyId(3).getM_pays()
                                + "\nPour plus d'information faite la commande **s!help**");
                    }
                }

                // message d'erreur;
            }
        }else{
            build.appendDescription("__**/!\\ERREUR/!\\**__ \n");
            build.appendDescription("Veuillez indiquer un nom de Groupe de serveur et/ou un pays correcte \nExemple : s!srv" +
                    Gsrv.takeGroupServerById(1).getM_title()+" "+Gsrv.takeGroupServerById(1).getServeurbyId(1).getM_pays()
                    + "\nPour plus d'information faite la commande **s!help**");
        }

        return build;
    }

    public EmbedBuilder CommandOn(GestionServer Gsrv){
        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat h = new SimpleDateFormat("hh:mm");

        Date currentTime_1 = new Date();

        String dateString = d.format(currentTime_1);
        String heureString = h.format(currentTime_1);
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("**SERVER-ON  [ " + heureString + " , " + dateString + " ]**");
        build.setColor(Color.red);
        build.appendDescription("\nVoici tous les serveurs online: \n");
        for(int i=0;i<Gsrv.getSize();i++){
            build.appendDescription("["+Gsrv.takeGroupServerById(i).getM_title()+"]\n");
            for (Serveur on:Gsrv.takeGroupServerById(i).getServeurON()) {
                String actif=""; if(on.getM_actif())actif="ONLINE"; else actif="OFFLINE";
                build.appendDescription("["+on.getM_pays()+"]"+on.getM_name()+"   "+on.getM_date()+"   "+actif+"\n");
            }
        }
        return build;
    }

    public EmbedBuilder CommandOff(GestionServer Gsrv){
        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat h = new SimpleDateFormat("hh:mm");

        Date currentTime_1 = new Date();

        String dateString = d.format(currentTime_1);
        String heureString = h.format(currentTime_1);
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("**SERVER-OFF  [ " + heureString + " , " + dateString + " ]**");
        build.setColor(Color.red);
        build.appendDescription("\nVoici tous les serveurs offline: \n");
        for(int i=0;i<Gsrv.getSize();i++){
            build.appendDescription("["+Gsrv.takeGroupServerById(i).getM_title()+"]\n");
            for (Serveur on:Gsrv.takeGroupServerById(i).getServeurOFF()) {
                String actif=""; if(on.getM_actif())actif="ONLINE"; else actif="OFFLINE";
                build.appendDescription("["+on.getM_pays()+"]"+on.getM_name()+"   "+on.getM_date()+"   "+actif+"\n");
            }
        }
        return build;
    }

    public EmbedBuilder ChangeServeur(GestionServer Gsrv){
        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat h = new SimpleDateFormat("hh:mm");

        Date currentTime_1 = new Date();

        String dateString = d.format(currentTime_1);
        String heureString = h.format(currentTime_1);
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("**ETAT_SERVEUR_CHANGE  [ " + heureString + " , " + dateString + " ]**");
        build.setColor(Color.red);
        build.appendDescription("\nVoici tous les serveurs : \n ");
        seeServerData(Gsrv, build);
        return build;
    }

    private void seeServerData(GestionServer Gsrv, EmbedBuilder build) {
        for (int j=0;j<Gsrv.getSize();j++) {
            GroupServer s = Gsrv.takeGroupServerById(j);
            build.appendDescription("\n[SERVER GROUP : "+s.getM_title()+"]\n\n");
            for(int i=0;i<s.getSize();i++){
                Serveur srv = s.getServeurbyId(i);
                String actif ="";
                if (srv.getM_actif()) actif="ONLINE"; else actif = "OFFLINE";
                build.appendDescription("["+srv.getM_pays()+"]"+srv.getM_name()+"  "+srv.getM_date()+"  "+actif+"\n");
            }
        }
    }

    public EmbedBuilder CommandClean(MessageReceivedEvent event,String msg){
        EmbedBuilder build = new EmbedBuilder();
        System.out.println(msg.split(" ").length);
        if (msg.split(" ").length>2||msg.split(" ").length<=1){
            build.appendDescription("Paramètres incorrects");
        }else {
            int nb = Integer.parseInt(msg.split(" ")[1]);
            List<Message> m_msg;
            if (nb>100){
                build.appendDescription("indiquez un nombre entre 1 et 100");
            }else {
                m_msg=event.getTextChannel().getHistory().retrievePast(nb).complete();
                event.getTextChannel().deleteMessages(m_msg).queue();
                build.appendDescription(nb+" messages supprimés avec succés");
            }
        }
        return build;
    }
}
