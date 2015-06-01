How to use springextjs

# Introduction #

The goal of this project is to easily integrate Spring3 with ExtJS Direct specification.
The main idea was to write Spring MVC controller as you would for any project, and let springextjs
handle the call translation down to ExtJS.

I have tried to make it as simple as possible but I am sure there is much room for improvement.


Polling has not been completely worked out mainly because it would make more sense to do it using newer
NIO supported Servlets, which are very much tied to app servers currently. This will be worked in soon
once Servlet 3 is out and adopted in Spring MVC for controller creation. Also, will work nicely when
ExtJS direct makes use of WebSockets, otherwise, in my view, polling is currently not worth supporting
in this project but the com.google.code.springextjs.direct.ExtJsDirectRemotingResponse can be used to
create polling responses.


# Setting Up Controller #

This is as simple as extending a the base abstract controller

```
@Controller
@RequestMapping(value = "/extjs/remoting")
public class DemoExtJsDirectRemotingController extends ExtJsDirectRemotingController implements MessageSourceAware{
...
}
```

Note that implementing MessageSourceAware is a Spring thing if you want to access localized messages in your controller,
if not, leave it out.

# Non Form Handler Methods in Controller #

All methods in the controller which are non form submission should be annotated

```
@ExtJsRemotingMethod
public String doEcho (String message){
	return message;
}
```

Following the flexibility of Spring3, the input parameters can be any primitive or wrapper type, String, ServletRequest, ServletResponse, Locale, or any POJO which the Jackson Object Mapper can deserialize to from a JSON String.

# Form Handler Methods in Controller #

All methods which are form handlers should be annotated using Springs standard annotation of:

```
@RequestMapping (value="/updateUser", method = RequestMethod.POST)
```

And return ModelAndView. They can also use any standard Spring annotations as needed, so a method may look something like this:

```
@ExtJsRemotingMethod
@RequestMapping (value="/updateForm", method = RequestMethod.POST)//standard Spring form post annotation
public ModelAndView updateForm(Locale locale, HttpServletRequest request, @Valid Form form, BindingResult result){//standard Spring supported parameters
	String message = null;
	if (!result.hasErrors()){
	    message = this.messageSource.getMessage("success.message", null, locale);
	}
	ModelAndView mnv = new ModelAndView();
	mnv.addObject("message", message);
	return mnv;
}
```

The parameters are limited to what Spring limits them to.

Form submits can take full advantage of Spring localized field validations and render validation responses in ExtJS components (see example application).

# Form Handler Method Results #

The return value of a form handler method should return Spring's ModelAndView object.
Your Spring app context should have the MVC interceptor to handle form responses:

```
<mvc:interceptors>
	<bean class="com.google.code.springextjs.direct.interceptor.ExtJsDirectRemotingFormResponseInterceptor"/>
</mvc:interceptors>

```

So, you Spring context file may look like:

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
		http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-3.0.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd"
       default-autowire="byName">

    <context:component-scan base-package="com.google.code.springextjs"/>
    <mvc:annotation-driven/>

    <mvc:interceptors>
        <!-- Changes the locale when a 'locale' request parameter is sent; e.g. /?locale=de -->
        <bean class="org.springframework.web.servlet.i18n.LocaleChangeInterceptor">
            <property name="paramName" value="locale"/>
        </bean>
        <bean class="com.google.code.springextjs.direct.interceptor.ExtJsDirectRemotingFormResponseInterceptor"/>

    </mvc:interceptors>

    <!-- Saves a locale change using a cookie -->
    <bean id="localeResolver" class="org.springframework.web.servlet.i18n.CookieLocaleResolver">
        <property name="cookieName" value="locale"/>
        <property name="cookieMaxAge" value="-1"/>
    </bean>

    <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
        <property name="basenames">
            <list>
                <value>messages</value>
            </list>
        </property>
    </bean>


</beans>
```


# Handling ExtJS Remoting Form Load API Calls #

Form loads are to be implemented in non form submission controller methods with any POJO return type which can be serialized by the Jackson object mapper to a JSON string.
Any remoting method which is a form load must contain the **formLoad = true** @ExtJsRemotingMethod annotation attribute.

```
@ExtJsRemotingMethod (formLoad = true)
public Form loadForm (String name, String email){
	Form form = new Form();
	form.setName(name);
	form.setEmail(email);
	return form;
}
```

# Setting Up Client JavaScript #

There are multiple ways to do this.

**Using JSP scriptlets in page directly:**

Use the utility method to output the remoting API JSON in the browser:

```
<script type="text/javascript"> 
	<%= ExtJsDirectRemotingApiUtil.getExtDirectRemotingApiString(
			request.getContextPath() + "/controller/extjs/remoting/router",
			DemoExtJsDirectRemotingController.class,
			"TestAction")
        %>                  
</script>
```

This will auto generate the remoting code off your controller like:

```
TestAction = {....}
```

Where **TestAction** will be the name of the API ExtJS would expect, so init code will look like:

```
Ext.onReady(function(){

    Ext.QuickTips.init();//needed to load pretty quick tip roll overs
    Ext.Direct.addProvider(
        TestAction
    );
    ....
});
```

If you do not pass the name directly such as **TestAction** in the example, or pass null, then the Controller class name will be the API name by default.


You can just as easily call the utility on the server and pass the generated ExtJS remoting API to the browser using an init Ajax call then load the API in ExtJS as stated above.

Another option is to use the URL directly by appending /api to your controller endpoint URL. For example, if you controller is mapped:

```
@Controller
@RequestMapping(value = "/extjs/remoting")
public class DemoExtJsDirectRemotingController extends ExtJsDirectRemotingController implements MessageSourceAware{
...
}
```

Then the API URLto add to the script src element would look like:

```
<script type="text/javascript" src="http://localhost:8000/springextjs/controller/extjs/remoting/api"></script>
```

# Router End Point #

By default the router RequestMapping is /router. So if you implemented a router controller with the following Spring annotation:

```
@RequestMapping(value = "/extjs/remoting")
```

Then the URL will be /extjs/remoting/router. To change the default "/router" mapping, simply re-implement the "router" method in the base controller and update the mapping as desired.

# Misc. Section #

due to bug in ExtJs, tree loading controller methods must have at least one param like:

```
@ExtJsDirectRemotingAnnotations.ExtJsRemotingMethod
    public List<ExtJsTreeNode> getTree (String node){//bug in ExtJs 3.2.1 and prior requires treeload method to accept
                                                    // at least one parameter or UI node which was expanded keeps
                                                    //spinning with loading animation

        List<ExtJsTreeNode> list = new ArrayList<ExtJsTreeNode>();
        list.add(new ExtJsTreeNode("n1", "Node 1", true));
        list.add(new ExtJsTreeNode("n2", "Node 2", true));
        list.add(new ExtJsTreeNode("n3", "Node 3", true));
        return list;
}
```

Otherwise a response will be generated but the TreeLoader loading animation will get stuck.
