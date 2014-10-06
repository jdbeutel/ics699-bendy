<script>
    webshims.setOptions('forms', {
        lazyCustomMessages: true,
        replaceValidationUI: true,
        addValidators: true,
        iVal: {
            sel: '.ws-validate',
            handleBubble: false,
            recheckDelay: 5000,  // after 5 second pause, or immediately on blur
            submitCheck: true,

            // bootstrap specific classes
            ${raw(indent ? "errorBoxClass: 'col-sm-offset-4 col-sm-8'," : '')}
            errorMessageClass: 'help-block',
            successWrapperClass: 'has-success',
            errorWrapperClass: 'has-error',
            fieldWrapper: '.form-group'
        }
    });
    webshims.polyfill('forms');
</script>
