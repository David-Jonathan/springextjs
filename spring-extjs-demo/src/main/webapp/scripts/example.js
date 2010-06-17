Ext.onReady(function(){

    Ext.QuickTips.init();//needed to load pretty quick tip roll overs
    Ext.Direct.addProvider(
        TestAction
    );

    var out = new Ext.form.DisplayField({
        cls: 'x-form-text',
        id: 'out'
    });

    var text = new Ext.form.TextField({
        width: 300,
        emptyText: 'Echo input'
    });

    var call = new Ext.Button({
        text: 'Echo',
        handler: function(){
            TestAction.doEcho(text.getValue(), function(result, e){
                var t = e.getTransaction();
                out.append(String.format('<p><b>Successful call to {0}.{1} with response:</b><xmp>{2}</xmp></p>',
                       t.action, t.method, Ext.encode(result)));
                out.el.scroll('b', 100000, true);
            });
        }
    });

    var num = new Ext.form.TextField({
        width: 80,
        emptyText: 'Multiply x 8',
        style:  'text-align:left;'
    });

    var multiply = new Ext.Button({
        text: 'Multiply',
        handler: function(){
            TestAction.multiply(num.getValue(), function(result, e){
                var t = e.getTransaction();
                if(e.status){
                    out.append(String.format('<p><b>Successful call to {0}.{1} with response:</b><xmp>{2}</xmp></p>',
                        t.action, t.method, Ext.encode(result)));
                }else{
                    out.append(String.format('<p><b>Call to {0}.{1} failed with message:</b><xmp>{2}</xmp></p>',
                        t.action, t.method, e.message));
                }
                out.el.scroll('b', 100000, true);
            });
        }
    });

    text.on('specialkey', function(t, e){
        if(e.getKey() == e.ENTER){
            call.handler();
        }
    });

	num.on('specialkey', function(t, e){
        if(e.getKey() == e.ENTER){
            multiply.handler();
        }
    });

	var remoteMethodPanel = new Ext.Panel({
        title: 'Remote Method Call Example',
        //frame:true,
		width: 600,
		height: 300,
		layout:'fit',

		items: [out],
        bbar: [text, call, '-', num, multiply]
	});


    var basicInfoForm = new Ext.form.FormPanel({
        // configs for FormPanel
        buttons:[{
            text: 'Submit',
            handler: function(){
                basicInfoForm.getForm().submit({
                    success: function(form, action){
                       if  (action.result.message && action.result.message != '')
                            Ext.Msg.alert("Form Submitted", action.result.message);
                    }
                })
            }
        }],
        buttonAlign : 'left',
        bodyBorder: false,
        frame: true,
        // configs apply to child items
        defaults: {anchor: '-20'}, // provide some room on right for validation errors
        defaultType: 'textfield',
        items: [{
            fieldLabel: 'Name',
            name: 'name'
        },{
            fieldLabel: 'Email',
            name: 'email'
        },{
            fieldLabel: 'Company',
            name: 'company'
        },
        {
            xtype : 'radio',
            fieldLabel: '',
            name: 'locale',
            inputValue: "en",
            boxLabel : 'English',
            checked : true
        },
        {
            xtype : 'radio',
            fieldLabel: '',
            name: 'locale',
            inputValue: "es",
            boxLabel : 'Spanish'
        }],

        // configs for BasicForm
        api: {
            // The server-side method to call for load() requests
            load: TestAction.loadForm,
            // The server-side must mark the submit handler as a 'formHandler'
            submit: TestAction.updateForm
        },
        // specify the order for the passed params
        paramOrder: ['nameParam', 'emailParam']
    });

    var form = new Ext.Panel ({
        title: 'Form Example'
        ,items: [
            new Ext.Panel ({
                height: 100
                ,contentEl: 'form-info'
                ,autoScroll: true
            })
            ,basicInfoForm
        ]
    });

    var tree = new Ext.tree.TreePanel({
        region : 'west',
        split : true,
        width : 300,
        autoScroll: true,
        title: 'Tree Example',
        root: new Ext.tree.AsyncTreeNode({id: "root" , text: 'Nodes'}),
        loader: new Ext.tree.TreeLoader({
            directFn: TestAction.getTree
        }),
        tbar: [{
            text: 'Reload root',
            handler: function(){
                tree.getRootNode().reload();
            }
        }]
    });

    var viewport = new Ext.Viewport({
        layout:'border',
        items: [ new Ext.Panel({
                region: 'north',
                height: 50,
                cmargins: '5 0 0 0',
                html : '<h1>Ext.Direct Generic Remoting with SpringExtJS</h1>'

            })
            ,tree
            ,new Ext.TabPanel({
                region: 'center', // a center region is ALWAYS required for border layout
                deferredRender: false,
                activeTab: 0,     // first tab initially active
                items: [
                    new Ext.Panel ({
                        title : 'Feature Overview'
                        ,contentEl : 'overview'

                    })
                    ,remoteMethodPanel
                    ,form
                ]
        })]

    });
    viewport.doLayout(true, true);
    basicInfoForm.getForm().load({params:{nameParam: "John Doe", emailParam: "john.doe@gmail.com"}});
});
