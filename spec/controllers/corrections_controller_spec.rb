# Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

require 'spec_helper'

describe CorrectionsController do

  describe '#index' do
    let(:corrections) { mock(Array) }
    before(:each) do
      Correction.should_receive(:all).and_return(corrections)
      get :index
    end
    subject { controller }

    it { should render_template(:index) }
    it { should assign_to(:corrections).with(corrections) }
  end

  describe '#upload' do
    let(:sync_id) { 555 }
    let(:correction) { mock(Hash) }
    let(:status) { Hash[:sync_id => sync_id] }
    before(:each) do
      Correction.should_receive(:create_and_return_status).twice.with(correction).and_return(status)
      post :upload, :corrections => [correction, correction], :format => :json
    end
    subject { controller }

    it { should respond_with(:success) }
    it { should assign_to(:statuses).with([status, status]) }
  end

  # DEPRECATED
  describe 'sync action' do
    before(:each) do
      @corrections = [
        {'sync_id' => '101', 'current_text' => 'irregardless'},
        {'sync_id' => '102', 'current_text' => 'equally as'},
        {'sync_id' => '103', 'current_text' => 'pour over'}
      ]
    end

    it 'returns HTTP success' do
      post :sync, :corrections => @corrections, :format => :json
      response.should be_success
    end

    it 'accepts valid corrections' do
      post :sync, :corrections => @corrections, :format => :json
      JSON.parse(response.body).each do |status|
        status['status'].should == 'ok'
      end
    end

    it 'selectively rejects invalid corrections' do
      @corrections[1]['current_text'] = ''
      bad_sync_id = @corrections[1]['sync_id']
      post :sync, :corrections => @corrections, :format => :json
      JSON.parse(response.body).each do |status|
        if status['sync_id'] == bad_sync_id
          status['status'].should == 'error'
        else
          status['status'].should == 'ok'
        end
      end
    end

    it 'selectively rejects mass assignment attempts' do
      @corrections[2]['updated_at'] = 'dawn'
      bad_sync_id = @corrections[2]['sync_id']
      post :sync, :corrections => @corrections, :format => :json
      JSON.parse(response.body).each do |status|
        if status['sync_id'] == bad_sync_id
          status['status'].should == 'error'
        else
          status['status'].should == 'ok'
        end
      end
    end
  end

end
