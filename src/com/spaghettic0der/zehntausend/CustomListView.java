package com.spaghettic0der.zehntausend;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;

import java.util.Set;


public class CustomListView<T> extends ListView
{

    private EventHandler eventHandler = null;

    public CustomListView(ObservableList items)
    {
        super(items);
    }

    /**
     * Please copy this in the listView class. Thanks Oracle.
     */
    public void setHScrollBarEnabled(boolean value)
    {
        Set<Node> set = this.lookupAll("VirtualScrollBar");

        for (Node n : set)
        {
            ScrollBar bar = (ScrollBar) n;
            if (bar.getOrientation() == Orientation.HORIZONTAL)
            {
                if (value)
                {
                    bar.setVisible(true);
                    bar.setDisable(false);
                    bar.setStyle("-fx-opacity: 100%");
                }
                else
                {
                    bar.setVisible(false);
                    bar.setDisable(true);
                    bar.setStyle("-fx-opacity: 0%");
                }
            }
        }
    }

    /**
     * It's really not hard to build it into your listView oracle.
     */
    public void setSelectable(boolean value)
    {

        if (value)
        {
            if (eventHandler != null)
                removeEventFilter(MouseEvent.ANY, eventHandler);
        }
        else
        {
            eventHandler = new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    event.consume();
                }

            };
            addEventFilter(MouseEvent.ANY, eventHandler);
        }
    }
}

