# How-To i18nize a string/message

## (Optional) Define a bundle variable
In case you have multiple translatable texts in the current context, you can define a bundle variable of a subclass from class I18nResourceBundle (see [*i18n* (Internationalization) in TripleA](./i18n_languages.md).

Example `final I18nResourceBundle bundle = I18nEngineFramework.get();`
![insertBundleVariable](https://user-images.githubusercontent.com/10353640/153715338-79de822f-304f-4abb-8e5f-efa43b85a428.png)


## Extract the translatable string/message
![extractStringFromCode](https://user-images.githubusercontent.com/10353640/153717470-e59cccb8-b22a-4602-8498-532679952936.png)
1. Place your cursor in the translatable string you want to extract
2. Open quick fix and select an *I18nize ...* entry (Alt -> select -> Enter) to open the I18nize popup; if no quick fix is shown check the settings described in (see [*i18n* (Internationalization) in TripleA](./i18n_languages.md)
3. Choose *Properties file* (if you work in the same code area is should not change often)
![i18nizePopup](https://user-images.githubusercontent.com/10353640/153717524-c8633f6e-62f6-4ede-8492-f81000866981.png)
4. Select *Property key* (if a property key for the same text is already available the option *Use existing property* will be selected automatically)
5. Set a *Resource bundle expression*, e.g., `I18nEngineFramework.get()` or nothing (see section 3.)
6. Check the *Preview*
7. Confirm with button *OK*
![i18nizedResult](https://user-images.githubusercontent.com/10353640/153717549-b098e9b2-2f1a-426d-90f5-d6309e06c8dd.png)

## Hints for IntelliJ IDEA
- Adapt code template *I18nized Concatenation* (*Edit I18n template* link in I18nize popup section *Preview* or in *Settings > Editor > File and Code Templates* tab *Code*) to
`#if (${RESOURCE_BUNDLE} == "")
#set( $RESOURCE_BUNDLE = "bundle" )#end
${RESOURCE_BUNDLE}.getString("${PROPERTY_KEY}", ${PARAMETERS})`
![codeTemplateI18nizedConcatenation](https://user-images.githubusercontent.com/10353640/153716949-6db2e0c8-2ca6-4f1e-bb80-f605a64c8412.png)
