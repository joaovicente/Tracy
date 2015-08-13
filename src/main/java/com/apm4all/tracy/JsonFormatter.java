package com.apm4all.tracy;

public class JsonFormatter {
    static public void addJsonStringValue(StringBuilder sb, String key, String value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        sb.append("\""+key+"\":\""+value+"\"");
    }
    
    static public void addJsonLongValue(StringBuilder sb, String key, long value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        sb.append("\""+key+"\":"+value);
    }

    static public void addJsonIntValue(StringBuilder sb, String key, int value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        sb.append("\""+key+"\":"+value);
    }
}
