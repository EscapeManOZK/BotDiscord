package fr.Bot;

import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
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
        m_command.add(help);
        m_command.add(etat);
        m_command.add(server);
        m_command.add(on);
        m_command.add(off);
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
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("**ETAT**");
        build.setColor(Color.red);
        build.appendDescription("\nVoici tous les serveurs : \n ");
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
        return build;
    }

    public EmbedBuilder CommandServer(GestionServer Gsrv, String msg){
        String group = msg.split(" ")[1];
        String srv = msg.split(" ")[2];
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("**SERVEUR**");
        build.setColor(Color.red);
        if (Gsrv.contains(group,srv)){
            Serveur serv = Gsrv.getGroupeServerByName(group).getServeurbyName(srv);
            String actif ="";
            if (serv.getM_actif()) actif="ONLINE"; else actif = "OFFLINE";
            build.appendDescription("["+serv.getM_pays()+"]"+serv.getM_name()+"   "+serv.getM_date()+"   "+actif);
        }else{
            build .appendDescription("__**/!\\ERREUR/!\\**__ \n");
            if(Gsrv.getGroupeServerByName(group)==null){
                build.appendDescription("Veuillez indiquer un nom de Groupe de serveur correcte \nExemple :"+
                        Gsrv.takeGroupServerById(1).getM_title()
                        +"\nPour plus d'information faite la commande **s!help**");
            }else if (Gsrv.getGroupeServerByName(group).getServeurbyName(srv)==null){
                if (!srv.contains("[")&&srv.contains("]")){
                    build.appendDescription("Veuillez indiquer le Pays du server \nExemple :"+
                            Gsrv.takeGroupServerById(1).getServeurbyId(3).getM_pays());
                }else{
                    build.appendDescription("Veuillez indiquer un nom de server correcte \nExemple :"+
                            Gsrv.takeGroupServerById(1).getServeurbyId(3).getM_pays()
                            +"\nPour plus d'information faite la commande **s!help**");
                }
            }

            // message d'erreur;
        }

        return build;
    }

    public EmbedBuilder CommandOn(GestionServer Gsrv){
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("**SERVER-ON**");
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
        EmbedBuilder build = new EmbedBuilder();
        build.setTitle("**SERVER-OFF**");
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
}
