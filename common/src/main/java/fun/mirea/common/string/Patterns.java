package fun.mirea.common.string;

import java.util.regex.Pattern;

public class Patterns {

    public static final Pattern MENTION = Pattern.compile("@[A-Za-z0-9_]{3,16}");

    public static final Pattern URL = Pattern.compile("^(https?|http|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

}
