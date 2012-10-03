require 'factory_girl_rails'

Correction.delete_all
FactoryGirl.create(:correction, :current_text => 'Call me Ishmeal.')
FactoryGirl.create(:correction, :current_text => 'It was a bright cold day in April, and the clocks were stricking thirteen.')
FactoryGirl.create(:correction, :current_text => 'The sky above the port was the color of television, turned to a dead channel.')
