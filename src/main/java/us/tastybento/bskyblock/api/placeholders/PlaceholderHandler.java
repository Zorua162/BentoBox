package us.tastybento.bskyblock.api.placeholders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import us.tastybento.bskyblock.BSkyBlock;
import us.tastybento.bskyblock.api.commands.User;

/**
 * Handles hooks with other Placeholder APIs.
 *
 * @author Poslovitch, Tastybento
 */
public class PlaceholderHandler {
    private static final String PACKAGE = "us.tastybento.bskyblock.api.placeholders.hooks.";
    /**
     * List of API classes in the package specified above (except the Internal one)
     */
    private static final String[] HOOKS = {
            //TODO
    };

    private static List<PlaceholderAPIInterface> apis = new ArrayList<>();

    /**
     * Register placeholders and hooks
     * @param plugin
     */
    public static void register(BSkyBlock plugin){

        // Load Internal Placeholder API
        try{
            Class<?> clazz = Class.forName(PACKAGE + "InternalPlaceholderImpl");
            PlaceholderAPIInterface internal = (PlaceholderAPIInterface)clazz.newInstance();
            apis.add(internal);
        } catch (Exception e){
            // Should never happen.
            plugin.getLogger().severe("Failed to load default placeholder API");
        }

        // Load hooks
        for(String hook : HOOKS){
            if(plugin.getServer().getPluginManager().isPluginEnabled(hook)){
                try{
                    Class<?> clazz = Class.forName(PACKAGE + hook + "PlaceholderImpl");
                    PlaceholderAPIInterface api = (PlaceholderAPIInterface)clazz.newInstance();
                    if(api.register(plugin)){
                        plugin.getLogger().info("Hooked placeholders into " + hook);
                        apis.add(api);
                    } else {
                        plugin.getLogger().info("Failed to hook placeholders into " + hook);
                    }
                } catch (Exception e){
                    plugin.getLogger().info("Failed to hook placeholders into " + hook);
                }
            }
        }
    }

    /**
     * Unregister placeholder hooks
     * @param plugin
     */
    public static void unregister(BSkyBlock plugin){
        Iterator<PlaceholderAPIInterface> it = apis.iterator();
        while (it.hasNext()) {
            PlaceholderAPIInterface api = it.next();
            api.unregister(plugin);
            it.remove();
        }
    }

    /**
     * Replace placeholders in the message according to the receiver
     * @param receiver
     * @param message
     * @return updated message
     */
    public static String replacePlaceholders(User receiver, String message){
        for(PlaceholderAPIInterface api : apis){
            message = api.replacePlaceholders(receiver, message);
        }

        return message;
    }

    /**
     * @return true if APIs are registered (including Internal), otherwise false
     */
    public static boolean hasHooks(){
        return apis != null;
    }
}
