AUI().ready(function (A) {
    var allRemoveLinks = A.all('.delete-friend');
    allRemoveLinks.each(function (item) {
        item.on('click', function (e) {
            e.halt();
            var dialog = new A.Dialog({
                bodyContent:'Vill du verkligen ta bort denna vän?',
                buttons:[
                    {text:'Ta bort', handler:function (e) {
                        var removeUrl = item.get('href');
                        A.io.request(removeUrl, {
                            cache: false,
                            sync: true,
                            timeout: 5000,
                            method: 'post',
                            on: {
                                success: function() {
                                    var rowDiv = item.ancestor().ancestor();
                                    rowDiv.html('<span class="portlet-msg-info">Borttagen</span>');
                                    A.later(3000, this, function() {
                                        rowDiv.setStyle('display', 'none');
                                    }, [], false);
                                },
                                failure: function() {
                                    alert('Anropet misslyckades.');
                                }
                            }
                        });
                        this.close();
                    }},
                    {
                        text:'Avbryt', handler:function (e) {
                        this.close();
                    }}
                ],
                centered:true,
                constrain2view:true,
                destroyOnClose:true,
                draggable:true,
                height:120,
                modal:true,
                resizable:false,
                stack:true,
                title:'Bekräfta',
                width:250
            }).render();
        });
    });
});