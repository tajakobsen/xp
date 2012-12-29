(function ($) {
    'use strict';

    var windows =  AdminLiveEdit.model.component.Windows = function () {
        this.cssSelector = '[data-live-edit-type=window]';
        this.renderEmptyPlaceholders();
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    windows.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    windows.constructor = windows;

    var proto = windows.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.appendEmptyPlaceholder = function ($window) {
        var $placeholder = $('<div/>', {
            'class': 'live-edit-empty-window-placeholder',
            'html': 'Empty Window'
        });
        $window.append($placeholder);
    };


    proto.isWindowEmpty = function ($window) {
        return $($window).children().length === 0;
    };


    proto.renderEmptyPlaceholders = function () {
        var t = this;
        this.getAll().each(function (index) {
            var $window = $(this);
            var windowIsEmpty = t.isWindowEmpty($window);
            if (windowIsEmpty) {
                t.appendEmptyPlaceholder($window);
            }
        });
    };

}($liveedit));