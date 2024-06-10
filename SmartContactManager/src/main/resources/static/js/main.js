const search=()=>{
	//console.log("seacrhing");
	let query=$("#search-input").val();
	if(query=='')
	{
		$(".search-result").hide();
	}else{
		
		let url=`http://localhost:8282/search/${query}`;
		
		fetch(url)
		  .then((response)=>
		    {return response.json();
		  
		  })
		  .then((data)=>{
			 console.log(data); 
			 
			 let text=`<div class='list-group'>`;
			 data.forEach((contact)=>{
				 
				 text+=`<a href='/user/contactSingle/${contact.cId}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
			 });
			 
			 text+=`</div>`;
			 console.log(text);
			 $(".search-result").html(text);
			 $(".search-result").show();
			 
		  });
		
		
		
		
	}
};
