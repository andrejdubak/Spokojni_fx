package com.example.spokojni;

import com.example.spokojni.backend.db.DB;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://stackoverflow.com/questions/24205093/how-to-create-a-custom-appender-in-log4j2
//Prevzaty adapter
@Plugin(name = "MyCustomAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class MyCustomAppender extends AbstractAppender {

    protected MyCustomAppender(String name, Filter filter,
                               Layout<? extends Serializable> layout, final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @PluginFactory
    public static MyCustomAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {
        if (name == null) {
            LOGGER.error("No name provided");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new MyCustomAppender(name, filter, layout, true);
    }

    @Override
    public void append(LogEvent logEvent) {

        try {
            final byte[] bytes = getLayout().toByteArray(logEvent);
            String log = new String(bytes, StandardCharsets.UTF_8);

            //Osetrenie aby sa do databazy nezapisovali logy vytvorene kniznicou kalendara
            if (log.contains("[DEBUG]")) return;
            if (log.contains("CalendarParserImpl")) return;
            // Nadviazanie spojenia s databazou
            try {
                DB.makeConn();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
            // Ak sa pri logoch nachadza id pouzivatela
            if (log.toLowerCase().contains("log_user_id:")) {
                //Kontrola ci je zapisane id pouzivatela v logu
                Matcher matcher = Pattern.compile("\\d+").matcher(log);
                boolean match = matcher.find();
                if (match) {
                    int userId = Integer.parseInt(matcher.group()); // Ziskanie id pouzivatela z logu
                    // Zavolanie funkcie na vlozenie logu do databazy a roztriedenie logov podla typu
                    if (log.toLowerCase().contains("[info]")) {

                        DB.log(log.replaceFirst("log_user_id:\\d+", ""), 6, userId);
                    }

                    if (log.toLowerCase().contains("[error]")) {
                        DB.log(log.replaceFirst("log_user_id:\\d+", ""), 3, userId);
                    }

                    if (log.toLowerCase().contains("[warn]")) {
                        DB.log(log.replaceFirst("log_user_id:\\d+", ""), 4, userId);
                    }

                }
                // Ak pri logoch neexsistuje id pouzivatela
            } else {

                if (log.toLowerCase().contains("[info]")) {
                    DB.log(log, 6);
                }

                if (log.toLowerCase().contains("[error]")) {
                    DB.log(log, 3);
                }

                if (log.toLowerCase().contains("[warn]")) {
                    DB.log(log, 4);
                }
            }


        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        }
    }
}