package org.vaadin.example;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route
@PWA(name = "Vaadin Application",
        shortName = "Vaadin App",
        description = "This is an example Vaadin application.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport("./styles/label-title.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param markov The message service. Automatically injected Spring managed bean.
     */
    public MainView(@Autowired MarkovTextGenerator markov) {

        // Label for prefix
        Label poolSizeText = new Label("Enter the desired random generation phrase lengths to be");
        poolSizeText.addClassName("propsId");
        // Prefix Parameter Input
        IntegerField poolSizeInput = new IntegerField ("Size of options");
        poolSizeInput.setValue(1);

        // Label for suffix
        Label outputSizeText = new Label("Enter the maximum desired length of the text generation to be");
        outputSizeText.addClassName("propsId");
        // Suffix Parameter Input
        IntegerField  outputSizeInput = new IntegerField ("Max Output Size");
        outputSizeInput.setValue(1);

        // Input for user to input a source file
        Label uploadText = new Label("Upload your source file here");
        uploadText.addClassName("propsId");
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload singleFileUpload = new Upload(memoryBuffer);
        // only allow txt files
        singleFileUpload.setAcceptedFileTypes("application/txt", ".txt");
        AtomicReference<InputStream> outputValue = new AtomicReference<>();
        singleFileUpload.addSucceededListener(event -> {
            // Get information about the uploaded file
            InputStream fileData = memoryBuffer.getInputStream();
            outputValue.set(fileData);
        });
        // only limit to one drag and drop
        singleFileUpload.setDropAllowed(true);


        // Button click listeners can be defined as lambda expressions
        Button submitButton = new Button("Run Markov's Magic",
                e -> {
            if (poolSizeInput.getValue() > outputSizeInput.getValue()) {
                Notification.show("Uh Oh!! pool size can't be larger than the output value!!");
                UI.getCurrent().getPage().reload();
            } else {
                try {
                    Notification.show(markov.generateTextWithMarkov(outputValue.get(),
                            poolSizeInput.getValue(),
                            outputSizeInput.getValue()));
                    // file needs to be reset so that the button can generate a new markov chain
                    outputValue.get().reset();

                } catch (IllegalArgumentException err1) {
                    Notification.show("Uh Oh!! Output value can't be larger than the number of words in your upload!!");
                    UI.getCurrent().getPage().reload();
                } catch (IOException err2) {
                    err2.printStackTrace();
                }
            }});
        submitButton.addThemeVariants(ButtonVariant.LUMO_LARGE);

        Button resetButton = new Button("Reset",
                e -> {
                    UI.getCurrent().getPage().reload();
                });
        resetButton.addThemeVariants(ButtonVariant.LUMO_ERROR);


        // Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        addClassName("centered-content");

        add(uploadText, singleFileUpload, poolSizeText, poolSizeInput,outputSizeText,outputSizeInput, submitButton, resetButton);
    }

}