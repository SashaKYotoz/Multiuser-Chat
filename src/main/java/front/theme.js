let switcher = document.getElementById("switch");
let isDark = switcher.checked;

if (localStorage.getItem("switcher")) {
	isDark = localStorage.getItem("switcher");
	if (isDark == "true") isDark = true;
	if (isDark == "false") isDark = false;
	checkTheme();
	switcher.checked = isDark;
}
switcher.addEventListener("change", ()=>{
	isDark = switcher.checked;
	checkTheme();
	localStorage.setItem("switcher",isDark);
})
function checkTheme() {
	if (isDark) {
		document.body.classList.remove("text-bg-light");
		document.body.classList.add("text-bg-dark");
	}else{
		document.body.classList.remove("text-bg-dark");
		document.body.classList.add("text-bg-light");
	}
}