package com.apm4all.tracy;

public class JsonFormatter {
    static public void addJsonStringValue(StringBuilder sb, String key, String value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        if (value.contains("\\") )   {
            value = value.replace("\\", "\\\\");
        }
        if (value.contains("\""))   {
            value = value.replace("\"", "\\\"");
        }
        sb.append("\""+key+"\":\""+value+"\"");
    }
    
    static public void addJsonLongValue(StringBuilder sb, String key, long value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        sb.append("\""+key+"\":"+value);
    }

    static public void addJsonFloatValue(StringBuilder sb, String key, float value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        sb.append("\""+key+"\":"+value);
    }
    
    static public void addJsonDoubleValue(StringBuilder sb, String key, double value, boolean first)	{
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
    
    static public void addJsonBooleanValue(StringBuilder sb, String key, boolean value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        sb.append("\""+key+"\":"+value);
    }
}
