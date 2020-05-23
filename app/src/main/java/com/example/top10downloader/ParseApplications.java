package com.example.top10downloader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

class ParseApplications {
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;

    ParseApplications() {
        this.applications = new ArrayList<>();
    }

    ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    void parse(String xmlData) {
        boolean status = true;
        FeedEntry currentRecord = null;
        boolean isEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        if (tagName.equalsIgnoreCase("entry")) {
                            isEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if (isEntry) {
                            if (tagName.equalsIgnoreCase("entry")) {
                                applications.add(currentRecord);
                                currentRecord = null;
                                isEntry = false;
                            } else if (tagName.equalsIgnoreCase("name")) {
                                currentRecord.setName(textValue);
                            } else if (tagName.equalsIgnoreCase("artist")) {
                                currentRecord.setArtist(textValue);
                            } else if (tagName.equalsIgnoreCase("releaseDate")) {
                                currentRecord.setReleaseDate(textValue);
                            } else if (tagName.equalsIgnoreCase("summary")) {
                                currentRecord.setSummary(textValue);
                            } else if (tagName.equalsIgnoreCase("image")) {
                                currentRecord.setImageUrl(textValue);
                            }
                        }
                        break;

                    default:
                        // do nothing
                }

                eventType = xpp.next();
            }

        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

    }
}
