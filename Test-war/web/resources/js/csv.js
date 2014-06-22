function handleLanguageAddRequest(xhr, status, args) {
    handleLanguageRequest(xhr, status, args, 'dlg_lang_add');
}

function handleLanguageSetRequest(xhr, status, args) {
    handleLanguageRequest(xhr, status, args, 'dlg_lang');
}

function handleLanguageRequest(xhr, status, args, id) {
    if(args.validationFailed || !args.saved) {
        PF(id).jq.effect("shake", {times:5}, 100);
    }
    else {
        PF(id).hide();
    }
}
